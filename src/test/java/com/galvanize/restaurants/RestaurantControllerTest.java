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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.ServletContext;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {
    private static String ERROR_MESSAGE="Restuarant name cant be blank";
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Autowired
    RestaurantRespository respository;

    @Before
    public void before() {
        respository.deleteAll();

    }

    @After
    public void after() {
        respository.deleteAll();

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
        Restaurant pizzaHut = new Restaurant("pizzaHut");
        respository.save(pizzaHut);

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
        assertThat(actual, contains(pizzaHut));
    }


    @Test
    public void addRestaturantReturnsSuccess() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant("pizzaHut");

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
    public void addRestaturantReturnsErrorCodeForBlank() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant("");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }
    @Test
    public void addRestaturantReturnsErrorCodeForNULL() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(null);

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }
    @Test
    public void addRestaturantReturnsErrorCodeForSpace() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant("   ");

        //exercise
        final String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(pizzaHut))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();


        //Assert
        assertThat(response, is(ERROR_MESSAGE));



    }

}
