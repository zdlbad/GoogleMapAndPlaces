package com.example.zhangdonglin.googlemapsandplace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.example.zhangdonglin.googlemapsandplace.Parking;


public class ParkingTest {
    private Parking parking;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetNorth(){
        parking.setNorth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetSouth(){
        parking.setSouth(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetEast(){
        parking.setEast(null);
    }

    @Test (expected = NullPointerException.class)
    public void testNullsetWest(){
        parking.setWest(null);
    }

    @Test
    public void testMakeAPIConnection(){
        boolean conn = parking.MakeAPIConnection();
        assertEquals("Connection is made","true",conn);
    }

}
