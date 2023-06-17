package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        // check user
        Optional<User> optionalUser = userRepository3.findById(userId);
        if(!optionalUser.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        User user = optionalUser.get();

        // check parkingLot

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        SpotType spotType ;
        if (numberOfWheels<=2) spotType = SpotType.TWO_WHEELER;
        else if(numberOfWheels<=4) spotType = SpotType.FOUR_WHEELER;
        else spotType = SpotType.OTHERS;

        List<Spot> spotList = parkingLot.getSpotList();

        Spot spot = new Spot();

        for(Spot spot1 : spotList){
            if(!spot1.getOccupied()){
                if( getSpotTypeInNumber(spotType) <= getSpotTypeInNumber(spot1.getSpotType())){
                    if(spot.getPricePerHour() == 0){
                        spot =spot1;
                    }
                    else{
                        if(spot.getPricePerHour() > spot1.getPricePerHour()){
                            spot = spot1;
                        }
                    }
                }
            }

        }

        if(spot.getPricePerHour() == 0){
            throw new Exception("Cannot make reservation");
        }

        spot.setOccupied(true);

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(spot);

        user.getReservationList().add(reservation);
        spot.getReservationList().add(reservation);

        spotRepository3.save(spot);
        return reservationRepository3.save(reservation);


    }

    public static int getSpotTypeInNumber(SpotType spotType){
        if(SpotType.TWO_WHEELER == spotType) return 2;
        else if(SpotType.FOUR_WHEELER == spotType) return 4;
        return 5;
    }
}
