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

import java.util.ArrayList;
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
    private static String ERROR_MESSAGE = "Restaurant name can't be blank";
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Autowired
    RestaurantRespository restaurantRespository;
    @Autowired
    ReviewRepository reviewRepository;

    @Before
    public void before() {
       // restaurantRespository.deleteAll();

    }

    @After
    public void after() {
      //  restaurantRespository.deleteAll();

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
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE, "pizzaHut");
        restaurantRespository.save(pizzaHut);

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
        assertThat(actual, contains(
                hasProperty("name", is("pizzaHut"))
        ));
    }


    @Test
    public void addRestaurantReturnsSuccess() throws Exception {

        //setup
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE, "pizzaHut");

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
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE, "");

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
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE, null);

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
        Restaurant pizzaHut = new Restaurant(Long.MIN_VALUE, "   ");

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
        Restaurant restaurant = new Restaurant(Long.MIN_VALUE, "Fred's");
        restaurantRespository.save(restaurant);
        Iterable<Restaurant> restaurants = restaurantRespository.findAll();
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
    public void updateRestaurantUpdatesRestaurantSuccessfully() throws Exception {
        //setup
        Restaurant restaurant = new Restaurant(Long.MIN_VALUE, "PizzaHut");
        restaurantRespository.save(restaurant);
        Iterable<Restaurant> restaurants = restaurantRespository.findAll();
        Long id = restaurants.iterator().next().getId();


        //restaurant to be updated
        Restaurant updaterestaurant = new Restaurant(id, "test");

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

    @Test
    public void updateRestaurantDoesNotUpdatesRestaurant() throws Exception {

        Restaurant updaterestaurant = new Restaurant(Long.MIN_VALUE, "Test-100");

        final String response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/restaurants/{id}", 100)
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(updaterestaurant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //Assert
        assertThat(response, is("Not updated"));

    }

    @Test
    public void addReviewForRestaurantReturnsBadRequestIfRestaurantNotFound() throws Exception {

        Review review = new Review(1l, "test restaurant comment");


        final String response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/restaurants/{id}/reviews", 100)
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(review))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Assert
        assertThat(response, is("Error saving Review"));


    }

    @Test
    public void addOneReviewToRestaurantReturnsReview() throws Exception {

        Restaurant restaurant = new Restaurant(Long.MIN_VALUE, "Test-100");
        restaurantRespository.save(restaurant);
        Iterable<Restaurant> restaurantAdded = restaurantRespository.findAll();
        Long id = restaurantAdded.iterator().next().getId();
        Review review = new Review(1l, "test restaurant comment");


        final String response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/restaurants/{id}/reviews", id)
                .accept(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(review))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("response*******" + response);


        Review actual = OBJECT_MAPPER.readValue(response, Review.class);

        //Assert
        assertThat(actual.getId(), is(any(Long.class)));
        assertThat(actual.getComment(), is("test restaurant comment"));

    }
    @Test
    public void getReviewsForRestaurantReturnsReviews() throws Exception {
        Restaurant restaurant = new Restaurant(Long.MIN_VALUE, "Test-100");

        List<Review> reviewList = new ArrayList<>();
        Review review = new Review(1l, "test");
        reviewList.add(review);
        restaurant.setReviews(reviewList);
        restaurantRespository.save(restaurant);
        Iterable<Restaurant> restaurantAdded = restaurantRespository.findAll();
        Long id = restaurantAdded.iterator().next().getId();



        String response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/restaurants/{id}/reviews",id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Review> actual= OBJECT_MAPPER.readValue(response, new TypeReference<List<Review>>(){});
        assertThat(actual, contains(hasProperty("comment",is("test"))));

    }

}
