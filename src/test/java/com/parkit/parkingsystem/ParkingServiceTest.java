package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    
    Ticket ticket = new Ticket();
    
    ParkingSpot parkingSpot ;
    
    @BeforeEach
    private void setUpPerTest() {
        try {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
    
    @Test
    public void processIncomingVehicleTest() throws Exception {
        
        when(ticketDAO.isRecurring("ABCDEF")).thenReturn(false);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); 
        when(inputReaderUtil.readSelection()).thenReturn(1);

     	parkingService.processIncomingVehicle();
     	
    	verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    	
    	ArgumentCaptor<ParkingSpot> captor = ArgumentCaptor.forClass(ParkingSpot.class);
    	verify(parkingSpotDAO, Mockito.times(1)).updateParking(captor.capture());
    	
    	ParkingSpot actual = captor.getValue();
    	Assertions.assertAll("actual",
    			() -> Assertions.assertEquals(1, actual.getId()),
    			() -> Assertions.assertEquals(ParkingType.CAR, actual.getParkingType()),
    			() -> Assertions.assertEquals(false, actual.isAvailable())
		);
    	
    	ArgumentCaptor<Ticket> captorTicket = ArgumentCaptor.forClass(Ticket.class);
    	verify(ticketDAO, Mockito.times(1)).saveTicket(captorTicket.capture());
    	
    	Ticket actualTicket = captorTicket.getValue();
    	Assertions.assertAll("actualTicket",
    			() -> Assertions.assertEquals("ABCDEF", actualTicket.getVehicleRegNumber()),
    			() -> Assertions.assertEquals(false, actualTicket.getRecurring()),
    			() -> Assertions.assertEquals(0, actualTicket.getPrice()),
    			() -> Assertions.assertEquals(null, actualTicket.getOutTime())
    		);
    }
    
    @Test
    public void badGetNextParkingNumberIfAvailable() {
    	Assertions.assertThrows(Exception.class, () -> {
    		new ParkingSpot(0, null, false);
    	});
    		
    }

    @Test
    public void getNextParkingNumberIfAvailableTest() {

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); 
        when(inputReaderUtil.readSelection()).thenReturn(1);
        
    	parkingService.getNextParkingNumberIfAvailable();
    }
    
    @Test
    public void processExitingVehicleTest() throws Exception{

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setRecurring(true); 
        
        parkingService.processExitingVehicle();
        
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        Assertions.assertEquals(Precision.round(0.75*0.95, 2), ticket.getPrice()); // Le prix calculé doit être réduit de 5%
    }
    
}
