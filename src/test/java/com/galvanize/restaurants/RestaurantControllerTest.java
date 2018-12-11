package com.galvanize.restaurants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {
    private static String ERROR_MESSAGE="Restaurant name can't be blank";
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Autowired
    RestaurantRespository repository;

    @Before
    public void before() {
        repository.deleteAll();

    }

    @After
    public void after() {
        repository.deleteAll();

    }

    @Test
    public void getRestaurantsReturnsEmptyList() throws Exception {

        //setup

        //exercise
        final String actual = mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        //Assert

        assertThat(actual, is(("[]")));

    }


    @Test
    public void getRestaurantsReturnOneRestaturant() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE,"pizzaHut");
        repository.save(pizzaHut);

        //exercise
        final String response = mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        List<Restaurant> actual = OBJECT_MAPPER.readValue(response,
                new TypeReference<List<Restaurant>>() {
                });

        //Assert
        assertThat(actual,contains(
                hasProperty("name", is("pizzaHut"))
        ));
    }


    @Test
    public void addRestaurantReturnsSuccess() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE,"pizzaHut");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        Restaurant actual = OBJECT_MAPPER.readValue(response, Restaurant.class);

        //Assert
        assertThat(actual.getId(), is(any(Long.class)));
        assertThat(actual.getName(), is("pizzaHut"));

    }
    @Test
    public void addRestaurantReturnsErrorCodeForBlank() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE,"");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getHeader("Message");

        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }
    @Test
    public void addRestaurantReturnsErrorCodeForNULL() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE,null);

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getHeader("Message");

        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }
    @Test
    public void addRestaurantReturnsErrorCodeForSpace() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE,"   ");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getHeader("Message");




        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }
    @Test
    public void deleteRestaurantReturnsErrorCodeForNotFound() throws Exception {

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/restaurants/{id}", "11")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse()
                .getContentAsString();

        //Assert
        assertThat(response, is("Restaurant not found. Cannot be deleted"));



    }
    @Test
    public void deleteRestaurantDeletesRestaurantAndReturnsSuccessCode() throws Exception {
        //setup
        Restaurant restaurant = new Restaurant(Long.MIN_VALUE,"Fred's");
        repository.save(restaurant);
        Iterable<Restaurant> restaurants = repository.findAll();
        Long id = restaurants.iterator().next().getId();

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/restaurants/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString();


        //Assert
        assertThat(response, is("Restaurant is deleted"));

    }

    @Test
    public void updateRestaurantUpdatesRestaurantSuccessfully() throws Exception{
        //setup
        Restaurant restaurant = new Restaurant(Long.MIN_VALUE,"PizzaHut");
        repository.save(restaurant);
        Iterable<Restaurant> restaurants = repository.findAll();
        Long id = restaurants.iterator().next().getId();


        //restaurant to be updated
        Restaurant updaterestaurant = new Restaurant(id,"test");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/restaurants/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(updaterestaurant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Restaurant actual = OBJECT_MAPPER.readValue(response, Restaurant.class);
        //Assert
        assertThat(actual.getName(), is("test"));
    }

}
