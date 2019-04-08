package com.thomas.findyourbasketball;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class isNearbyTest {

    private String name;
    private double input_lat;
    private double input_lng;
    private boolean expected_is_nearby;
    private LatLng global_location;
    private double distance;

    @Before
    public void init(){
        Court court = new Court(name, input_lat, input_lng);
    }

    public isNearbyTest(String name, double input_lat, double input_lng, boolean expected_is_nearby, LatLng global_location, double distance){
        this.name = name;
        this.input_lat = input_lat;
        this.input_lng = input_lng;
        this.expected_is_nearby = expected_is_nearby;
        this.global_location = global_location;
        this.distance = distance;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][] {
                //Normal Data Testing
                {"Test 0",0,0,true, new LatLng(0.1, 0.1), 0.3 },
                {"Test 1",54.2,-0.23,true, new LatLng(53.8, -0.23), 0.3 },
                {"Test 2",88.9,50.0,true, new LatLng(88.3, 49.5), 0.6 },
                // Erroneous Data Testing
                {"Test 3",0,0,false, new LatLng(50, 50), 0.3 },
                {"Test 4",50,181,false, new LatLng(50, 179), 0.3 },
                // Extreme Data Testing
                {"Test 5",-23,180.5,true, new LatLng(-23, -179.2), 0.3 },
                {"Test 6",100,50,true, new LatLng(89.5, 50), 0.3 }
        });
    }

    @Test
    public void testCourt_isNearby(){
        Court court = new Court(name, input_lat, input_lng);
        System.out.println("Court params: " + court.toString() +", "+ "[GlobalLocation, Distance] = ["+global_location+", "+distance+"]" );
        System.out.println("Expected:["+ name +  ", " + expected_is_nearby+"]");
        System.out.println("Actual:["+name+", "+court.isNearbyLng(distance, global_location)+"]");
        System.out.println("---------------------------------------");
        assertEquals(name, court.getName());
        assertEquals(expected_is_nearby, court.isNearbyLng(distance,global_location));
    }
}
