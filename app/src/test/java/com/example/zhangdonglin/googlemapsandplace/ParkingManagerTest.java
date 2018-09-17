package com.example.zhangdonglin.googlemapsandplace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class ParkingManagerTest {
    private ParkingManager parkingManager;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetNorth(){
        parkingManager.setNorth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetSouth(){
        parkingManager.setSouth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetEast(){
        parkingManager.setEast(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetWest(){
        parkingManager.setWest(null);
    }

    @Test
    public void testMakeAPIConnection(){
        boolean conn = parkingManager.MakeAPIConnection();
        assertEquals("Connection is made","true",conn);
    }

}
