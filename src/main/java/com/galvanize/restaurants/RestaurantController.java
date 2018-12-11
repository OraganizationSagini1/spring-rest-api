package com.galvanize.restaurants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import java.util.List;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private static String ERROR_MESSAGE="Restuarant name cant be blank";

    @Autowired
    RestaurantRespository respository;

    @GetMapping
    Iterable<Restaurant> getRestaurants  ()
    {
        return respository.findAll();
    }


    @PostMapping
    ResponseEntity addRestaurants(@RequestBody Restaurant restaurant) {
        if(restaurant.getName()==null || restaurant.getName()=="" || restaurant.getName().trim().length()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR_MESSAGE);

        }
        respository.save(restaurant);
        return ResponseEntity.status(HttpStatus.OK).body(restaurant);
    }







}
