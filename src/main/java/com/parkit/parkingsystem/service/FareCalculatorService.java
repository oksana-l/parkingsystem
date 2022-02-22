package com.parkit.parkingsystem.service;

import org.apache.commons.math3.util.Precision;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        // TODO: Some tests are failing here. Need to check if this logic is correct
        // int -> double, getHours -> getTime plus format ms for hours

        double duration = (outHour - inHour) / 60 / 60 / 1000;
        if (duration <= 0.5) {
        	ticket.setPrice(0);
        } else {
        	switch (ticket.getParkingSpot().getParkingType()){
	            case CAR: {
	            	double price = (duration - 0.5) * Fare.CAR_RATE_PER_HOUR;
	                ticket.setPrice(Precision.round(price,2));
	                break;
	            }
	            case BIKE: {
	            	double price = (duration - 0.5) * Fare.BIKE_RATE_PER_HOUR;
	                ticket.setPrice(Precision.round(price,2));
	                break;
	            }
	            default: throw new IllegalArgumentException("Unkown Parking Type");
	        }
        }
    }
}	
