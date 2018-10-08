package com.example.zhangdonglin.googlemapsandplace;

public class MyTools {

    public static Double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double p = 0.017453292519943295;    // Math.PI / 180
        Double result = 0.5 - Math.cos((lat2 - lat1) * p)/2 + Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;

        return 12742 * Math.asin(Math.sqrt(result)); // 2 * R; R = 6371 km
    }

    public static Double getDistanceFromLatLonInMeter(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double R = 6371.0; // Radius of the earth in km
        Double dLat = deg2rad(lat2-lat1);  // deg2rad below
        Double dLon = deg2rad(lon2-lon1);
        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c ; // Distance in Km
        return d * 1000; //return in meter
    }

    private static Double deg2rad(Double deg) {
        return deg * (Math.PI/180);
    }

    public static Double roundDouble(Double numberIn){
        return Math. round(numberIn * 100.0) / 100.0;
    }

    public static int roundDoubleWithOutDecimal(Double numberIn){
        return (int)Math. round(numberIn);
    }

    public static String captialFirstChar(String input){
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

//    public static int[] sumTwoIntArray(int[] array1, int[] array2){
//        int[] result = new int[array1.length];
//        for (int i = 0; i < array1.length; i++){
//            result[i] = array1[i] + array2[i];
//        }
//        return result;
//    }
}
