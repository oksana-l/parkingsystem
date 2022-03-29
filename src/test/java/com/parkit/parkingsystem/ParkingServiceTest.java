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
import com.parkit.parkingsystem.service.FareCalculatorService;
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
    @Mock
    private static FareCalculatorService fareCalculatorService;
    
    
    @BeforeEach
    private void setUpPerTest() {
    	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }
    
    @Test
    public void processIncomingVehicleTest() throws Exception {
        
        when(ticketDAO.isRecurring("ABCDEF")).thenReturn(false);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); 
        when(inputReaderUtil.readSelection()).thenReturn(1);
 
        parkingService.processIncomingVehicle();
        
        ArgumentCaptor<ParkingSpot> captor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(captor.capture());
        
        ParkingSpot actualSpot = captor.getValue();
        Assertions.assertAll("actualSpot",
                () -> Assertions.assertEquals(1, actualSpot.getId()),
                () -> Assertions.assertEquals(ParkingType.CAR, actualSpot.getParkingType()),
                () -> Assertions.assertEquals(false, actualSpot.isAvailable())
        );
        
        ArgumentCaptor<Ticket> captorTicket = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO, Mockito.times(1)).saveTicket(captorTicket.capture());
        
        Ticket expectedTicket = captorTicket.getValue();
        Assertions.assertAll("expectedTicket",
                () -> Assertions.assertEquals("ABCDEF", expectedTicket.getVehicleRegNumber()),
                () -> Assertions.assertEquals(false, expectedTicket.getRecurring()),
                () -> Assertions.assertEquals(0, expectedTicket.getPrice()),
                () -> Assertions.assertTrue(expectedTicket.getInTime().before(new Date())),
                () -> Assertions.assertNull(expectedTicket.getOutTime())
            );
    }

    @Test 
    public void incomingVehicleToFullParking() throws Exception {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); 
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService.processIncomingVehicle();
        
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any());
        verify(ticketDAO, Mockito.times(0)).saveTicket(any());
        verify(inputReaderUtil, Mockito.times(0)).readVehicleRegistrationNumber();
    }
    
    @Test
    public void processExitingVehicleTest() throws Exception{
 
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setRecurring(true); 
        
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
 
        parkingService.processExitingVehicle();
        
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        Assertions.assertEquals(Precision.round(0.75*0.95, 2), ticket.getPrice()); // Le prix calculé doit être réduit de 5%
   
 
    }
    
    @Test
    public void processExitingWhenVehicleNotExistTest() throws Exception {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
 
        parkingService.processExitingVehicle();

        verify(fareCalculatorService, Mockito.times(0)).calculateFare(any());
        verify(ticketDAO, Mockito.times(0)).updateTicket(any());
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any());
    }
    
}