package com.parkit.parkingsystem.model;

import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private boolean recurring;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
    	if (this.parkingSpot == null) {
    		return null;
    	}
        return new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
    	if (parkingSpot == null) {
    		this.parkingSpot = null;
    	} else {
    		this.parkingSpot = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
    	}
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
    
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
    	if (this.inTime == null) {
    		return null;
    	}
        return new Date(this.inTime.getTime());
    }

    public void setInTime(final Date inTime) {
    	if (inTime == null) {
    		this.inTime = null;
    	} else {
    		this.inTime = new Date(inTime.getTime());
    	}
    }

    public Date getOutTime() {
    	if (this.outTime == null) {
    		return null;
    	}
        return new Date(this.outTime.getTime());
    }

    public void setOutTime(Date outTime) {
    	if (outTime == null) {
    		this.outTime = null;
    	} else {
    		this.outTime = new Date(outTime.getTime());
    	}
    }
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(id, outTime, parkingSpot, price, recurring, vehicleRegNumber);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Ticket other = (Ticket) obj;
//		return id == other.id && Objects.equals(outTime, other.outTime)
//				&& Objects.equals(parkingSpot, other.parkingSpot)
//				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
//				&& recurring == other.recurring && Objects.equals(vehicleRegNumber, other.vehicleRegNumber);
//	}
//
//	@Override
//	public String toString() {
//		return "Ticket [id=" + id + ", parkingSpot=" + parkingSpot + ", vehicleRegNumber=" + vehicleRegNumber
//				+ ", recurring=" + recurring + ", price=" + price + ", inTime=" + inTime + ", outTime=" + outTime + "]";
//	}
    
    
}
