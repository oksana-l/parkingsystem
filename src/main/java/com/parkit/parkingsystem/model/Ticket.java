package com.parkit.parkingsystem.model;

import java.util.Date;
import java.util.Objects;

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
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
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
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

	@Override
	public int hashCode() {
		return Objects.hash(id, outTime, parkingSpot, price, recurring, vehicleRegNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		return id == other.id && Objects.equals(outTime, other.outTime)
				&& Objects.equals(parkingSpot, other.parkingSpot)
				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
				&& recurring == other.recurring && Objects.equals(vehicleRegNumber, other.vehicleRegNumber);
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", parkingSpot=" + parkingSpot + ", vehicleRegNumber=" + vehicleRegNumber
				+ ", recurring=" + recurring + ", price=" + price + ", inTime=" + inTime + ", outTime=" + outTime + "]";
	}
    
    
}
