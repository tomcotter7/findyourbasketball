package com.thomas.findyourbasketball;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CourtLatLngTest {

    private String name;
    private double input_lat;
    private double input_lng;
    private double expected_lat;
    private double expected_lng;

    @Before
    public void init(){
        Court court = new Court(name, input_lat, input_lng);
    }

    public CourtLatLngTest(String name, double input_lat, double input_lng, double expected_lat, double expected_lng) {
        this.name = name;
        this.input_lng = input_lng;
        this.input_lat = input_lat;
        this.expected_lat = expected_lat;
        this.expected_lng = expected_lng;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][] {
                //Normal Data Input
                {"Test0", 0, 0, 0, 0},
                {"Test1", 5, 10, 5, 10},
                //Erroneous Data Input - 100/-100 Latitude doesn't exist
                {"Test2", -100, 0, -90, 0},
                {"Test3", 100, 0, 90, 0},
                //Extreme Data Input - 190/-190 should wrap around to -170/170
                {"Test4", 0, 190 , 0, -170},
                {"Test5", 0, -190 , 0, 170},

        });
    }
    @Test
    public void testCourt_latLng(){
        Court court = new Court(name, input_lat, input_lng);
        System.out.println("Court params: " + court.toString());
        System.out.println("Expected:["+ name +  ", " + expected_lat + ", " + expected_lng + "]");
        assertEquals(name, court.getName());
        assertEquals(expected_lat, court.getLatitude(),0.0);
        assertEquals(expected_lng, court.getLongitude(),0.0);

    }


}
