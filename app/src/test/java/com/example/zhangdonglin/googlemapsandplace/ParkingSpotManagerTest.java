package com.example.zhangdonglin.googlemapsandplace;

import com.example.zhangdonglin.googlemapsandplace.Controller.ParkingSpotManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class ParkingSpotManagerTest {
    private ParkingSpotManager parkingSpotManager;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetNorth(){
        parkingSpotManager.setNorth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetSouth(){
        parkingSpotManager.setSouth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetEast(){
        parkingSpotManager.setEast(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetWest(){
        parkingSpotManager.setWest(null);
    }

    @Test
    public void testMakeAPIConnection(){
        boolean conn = parkingSpotManager.MakeAPIConnectionForLocation();
        assertEquals("Connection is made","true",conn);
    }

}
