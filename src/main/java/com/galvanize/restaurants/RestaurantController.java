package com.galvanize.restaurants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import java.util.List;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    RestaurantRespository respository;

    @GetMapping
    Iterable<Restaurant> getRestaurants  ()
    {
        return respository.findAll();
    }


    @PostMapping
    Restaurant addRestaurants(@RequestBody Restaurant restaurant) {
        respository.save(restaurant);
        return restaurant;
    }







}
