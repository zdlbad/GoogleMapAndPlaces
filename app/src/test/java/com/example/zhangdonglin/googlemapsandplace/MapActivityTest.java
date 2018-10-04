package com.example.zhangdonglin.googlemapsandplace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.example.zhangdonglin.googlemapsandplace.View.MapActivity;

public class MapActivityTest {
    private MapActivity map;
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    //Null Values cannot should not be accepted by the functions
    @Test (expected = Exception.class)
    public void testNullOnMapReady(){
        map.onMapReady(null);
    }

    @Test (expected = Exception.class)
    public void testNullGetLatlng() {
        map.getLatlng(null);
    }

    @Test (expected = Exception.class)
    public void testNullOnCreate() {
        map.onCreate(null);
    }

    @Test (expected = Exception.class)
    public void testNullToBounds(){
        map.toBounds(null,0);
    }

    @Test (expected = Exception.class)
    public void testOnActivityResult(){
        map.onActivityResult(0,0,null);
    }

    @Test (expected = Exception.class)
    public void testOnConnectionFailed(){
        map.onConnectionFailed(null);
    }

    @Test (expected = Exception.class)
    public void testOnRequestPermissionResult(){
        map.onRequestPermissionsResult(0,null,null);
    }

    @Test (expected = Exception.class)
    public void testOnRequestPermissionsResult(){
        int [] arr ={1,2,3,4};
        String[] st ={"abcd","efgh"};
        String[] a = st;
        map.onRequestPermissionsResult(-1,st,arr);
    }

}