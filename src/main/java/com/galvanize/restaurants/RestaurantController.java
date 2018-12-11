package com.galvanize.restaurants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final static String ERROR_MESSAGE="Restaurant name can't be blank";
    private final static String DELETE_MESSAGE="Restaurant not found. Cannot be deleted";

    @Autowired
    RestaurantRespository repository;

    @GetMapping
    Iterable<Restaurant> getRestaurants() {
        return repository.findAll();
    }

    @PostMapping
    ResponseEntity<Restaurant> addRestaurants(@RequestBody Restaurant restaurant) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if(restaurant.getName()==null || restaurant.getName()=="" || restaurant.getName().trim().length()==0){
            httpHeaders.set("Message", ERROR_MESSAGE);
            return new ResponseEntity<>(restaurant, httpHeaders, HttpStatus.BAD_REQUEST);
        }
        repository.save(restaurant);
        httpHeaders.set("Message","Post request success");
        return new ResponseEntity<>(restaurant, httpHeaders, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteRestaurant(@PathVariable long id) {
        Optional<Restaurant> restaurant = repository.findById(id);
        if(restaurant.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Restaurant is deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DELETE_MESSAGE);
        }
    }

    @PutMapping("/{id}")
    ResponseEntity updateRestaurant(@RequestBody Restaurant newRestaurant, @PathVariable long id) {
        Optional<Restaurant> oldRestaurant = repository.findById(id);
        if(oldRestaurant.isPresent())   {
            oldRestaurant.get().setName(newRestaurant.getName());
            repository.save(oldRestaurant.get());
            return ResponseEntity.status(HttpStatus.OK).body(oldRestaurant.get());
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not updated");
        }
    }
}