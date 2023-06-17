package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        List<Spot> spots = parkingLot.getSpotList();
        //Spot spot = null;
        List<Spot> spotListFollowsCondition = new ArrayList<>();

        for(Spot s : spots){
            if(s.getOccupied()==false){
                int capacity;
                if(s.getSpotType()==SpotType.TWO_WHEELER) {
                    capacity = 2;
                }
                else if (s.getSpotType()==SpotType.FOUR_WHEELER) {
                    capacity = 4;
                }
                else{
                    capacity = Integer.MAX_VALUE;
                }

                if(capacity>=numberOfWheels){
                    //spot = s;
                    spotListFollowsCondition.add(s);
                }
            }
        }

        //check whether spots are available in the list or not
        if(spotListFollowsCondition.isEmpty()){
            throw new Exception("Cannot make reservation");
        }

        //now final check for low price spot reservation
        Spot reserveSpot = null;
        int minimumPrice = Integer.MAX_VALUE;
        for(Spot spot : spotListFollowsCondition){
            int price = spot.getPricePerHour() * timeInHours;
            if(price<minimumPrice){
                minimumPrice = price;
                reserveSpot = spot;
            }
        }

        reserveSpot.setOccupied(true);

        //create reservation object
        Reservation reservation = new Reservation();
        reservation.setSpot(reserveSpot);
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setPayment(null);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(reserveSpot);

        return reservation;


    }

    public static int getSpotTypeInNumber(SpotType spotType){
        if(SpotType.TWO_WHEELER == spotType) return 2;
        else if(SpotType.FOUR_WHEELER == spotType) return 4;
        return 5;
    }
}
