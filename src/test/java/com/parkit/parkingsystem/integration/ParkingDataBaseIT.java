package com.parkit.parkingsystem.integration;
 
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
 
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
 
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;
    
    @Mock
    private static InputReaderUtil inputReaderUtil;
 
    @BeforeEach
    private void setUpPerTest() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        dataBasePrepareService.clearDataBaseEntries();
    }
 
    @AfterEach
    private void setUpAfterTest() {
    	dataBasePrepareService.clearDataBaseEntries();
    }
    
    
    // vérifier qu'un ticket est effectivement enregistré dans la base de données et que la table de stationnement est mise à jour avec la disponibilité
    @Test
    public void testParkingACar() throws Exception{

        parkingService.processIncomingVehicle();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket actualTicket = ticketDAO.getTicket("ABCDEF");
        Assertions.assertAll("actualTicket",
                () -> Assertions.assertEquals( "ABCDEF", actualTicket.getVehicleRegNumber()),
                () -> Assertions.assertEquals( parkingSpot, actualTicket.getParkingSpot()),
                () -> Assertions.assertFalse( actualTicket.getRecurring()),
                () -> Assertions.assertEquals( 0, actualTicket.getPrice()),
                () -> Assertions.assertNotNull(actualTicket.getInTime()),
                () -> Assertions.assertEquals( null, actualTicket.getOutTime())
            );
    }
 

    // vérifier que le tarif généré et l'heure de départ sont correctement renseignés dans la base de données
    @Test
    public void testParkingLotExit() throws Exception{
        testParkingACar();
        parkingService.processExitingVehicle();

        Ticket expectedTicket = ticketDAO.getTicket("ABCDEF");
        Assertions.assertAll("expectedTicket",
                () -> Assertions.assertEquals( "ABCDEF", expectedTicket.getVehicleRegNumber()),
                () -> Assertions.assertFalse( expectedTicket.getRecurring()),
                () -> Assertions.assertEquals( 0, expectedTicket.getPrice()),
                () -> Assertions.assertFalse( expectedTicket.getOutTime().before(expectedTicket.getInTime()))
            );
    }
 
}
 