package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setAddress(address);
        parkingLot.setName(name);

        return parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        Spot spot = new Spot();
        spot.setOccupied(false);
        spot.setParkingLot(parkingLot);
        spot.setPricePerHour(pricePerHour);

        if(numberOfWheels<=2)spot.setSpotType(SpotType.TWO_WHEELER);
        else if(numberOfWheels<=4) spot.setSpotType(SpotType.FOUR_WHEELER);
        else spot.setSpotType(SpotType.OTHERS);

        parkingLot.getSpotList().add(spot);
        parkingLotRepository1.save(parkingLot);

        return spot;
    }

    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> spot = spotRepository1.findById(spotId);
        if(spot.isPresent()) {
            ParkingLot parkingLot = spot.get().getParkingLot();
            parkingLot.getSpotList().remove(spot);
            spotRepository1.deleteById(spotId);
        }

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<Spot> spot = spotRepository1.findById(spotId);
        if(spot.isPresent()) {
            Spot spot1 = spot.get();
            ParkingLot parkingLot = spot1.getParkingLot();
            spot1.setPricePerHour(pricePerHour);
            parkingLotRepository1.save(parkingLot);
            return spotRepository1.save(spot1);
        }

        return null;

    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
