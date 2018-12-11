package com.galvanize.restaurants;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class RestaurantHealth implements HealthIndicator {
        @Override
        public Health health() {
            int errorCode = check();
            if (errorCode != 0) {
                return Health.down().withDetail("Error Code", errorCode).build();
            }
            return Health.up().build();
        }

    private int check() {
            LocalTime now = LocalTime.now();
            return now.getMinute()%2!=0?1:0;
    }
}