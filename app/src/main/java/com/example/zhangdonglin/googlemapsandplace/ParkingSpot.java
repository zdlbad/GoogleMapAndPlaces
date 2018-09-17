package com.example.zhangdonglin.googlemapsandplace;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//{":@computed_region_evbi_jbp8":"1","bay_id":"6227","lat":"-37.816737889952776",
// "location":{"type":"Point","coordinates":[144.945818509531,-37.816737889953]},
// "lon":"144.94581850953125","st_marker_id":"13332E","status":"Unoccupied"}

public class ParkingSpot {


    private Double lat;
    private Double lon;
    private String status;
    private int normalDuration;
    private int disDuration;
    private String disableOnly;
    private String needToPay;

    private String BayID;

    private String Description1;
    private String Description2;
    private String Description3;
    private String Description4;
    private String Description5;
    private String Description6;

    private String DeviceID;

    private String DisabilityExt1;
    private String DisabilityExt2;
    private String DisabilityExt3;
    private String DisabilityExt4;
    private String DisabilityExt5;
    private String DisabilityExt6;

    private String Duration1;
    private String Duration2;
    private String Duration3;
    private String Duration4;
    private String Duration5;
    private String Duration6;

    private String EffectiveOnPH1;
    private String EffectiveOnPH2;
    private String EffectiveOnPH3;
    private String EffectiveOnPH4;
    private String EffectiveOnPH5;
    private String EffectiveOnPH6;

    private String EndTime1;
    private String EndTime2;
    private String EndTime3;
    private String EndTime4;
    private String EndTime5;
    private String EndTime6;

    private String FromDay1;
    private String FromDay2;
    private String FromDay3;
    private String FromDay4;
    private String FromDay5;
    private String FromDay6;

    private String StartTime1;
    private String StartTime2;
    private String StartTime3;
    private String StartTime4;
    private String StartTime5;
    private String StartTime6;

    private String ToDay1;
    private String ToDay2;
    private String ToDay3;
    private String ToDay4;
    private String ToDay5;
    private String ToDay6;

    private String TypeDesc1;
    private String TypeDesc2;
    private String TypeDesc3;
    private String TypeDesc4;
    private String TypeDesc5;
    private String TypeDesc6;


    public ParkingSpot(){

        this.lat = 0.0;
        this.lon = 0.0;
        this.status = "";
        this.normalDuration = 0;
        this.disDuration = 0;
        this.disableOnly = "";
        this.needToPay = "";

        this.BayID = "";
        this.Description1 = "";
        this.Description2 = "";
        this.Description3 = "";
        this.Description4 = "";
        this.Description5 = "";
        this.Description6 = "";
        this.DeviceID = "";
        this.DisabilityExt1 = "";
        this.DisabilityExt2 = "";
        this.DisabilityExt3 = "";
        this.DisabilityExt4 = "";
        this.DisabilityExt5 = "";
        this.DisabilityExt6 = "";
        this.Duration1 = "";
        this.Duration2 = "";
        this.Duration3 = "";
        this.Duration4 = "";
        this.Duration5 = "";
        this.Duration6 = "";
        this.EffectiveOnPH1 = "";
        this.EffectiveOnPH2 = "";
        this.EffectiveOnPH3 = "";
        this.EffectiveOnPH4 = "";
        this.EffectiveOnPH5 = "";
        this.EffectiveOnPH6 = "";
        this.EndTime1 = "";
        this.EndTime2 = "";
        this.EndTime3 = "";
        this.EndTime4 = "";
        this.EndTime5 = "";
        this.EndTime6 = "";
        this.FromDay1 = "";
        this.FromDay2 = "";
        this.FromDay3 = "";
        this.FromDay4 = "";
        this.FromDay5 = "";
        this.FromDay6 = "";
        this.StartTime1 = "";
        this.StartTime2 = "";
        this.StartTime3 = "";
        this.StartTime4 = "";
        this.StartTime5 = "";
        this.StartTime6 = "";
        this.ToDay1 = "";
        this.ToDay2 = "";
        this.ToDay3 = "";
        this.ToDay4 = "";
        this.ToDay5 = "";
        this.ToDay6 = "";
        this.TypeDesc1 = "";
        this.TypeDesc2 = "";
        this.TypeDesc3 = "";
        this.TypeDesc4 = "";
        this.TypeDesc5 = "";
        this.TypeDesc6 = "";

    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNormalDuration() {
        return normalDuration;
    }

    public void setNormalDuration(int normalDuration) {
        this.normalDuration = normalDuration;
    }

    public int getDisDuration() {
        return disDuration;
    }

    public void setDisDuration(int disDuration) {
        this.disDuration = disDuration;
    }

    public String getDisableOnly() {
        return disableOnly;
    }

    public void setDisableOnly(String disableOnly) {
        this.disableOnly = disableOnly;
    }

    public String getNeedToPay() {
        return needToPay;
    }

    public void setNeedToPay(String needToPay) {
        this.needToPay = needToPay;
    }

    public String getBayID() {
        return BayID;
    }

    public void setBayID(String bayID) {
        BayID = bayID;
    }

    public String getDescription1() {
        return Description1;
    }

    public void setDescription1(String description1) {
        Description1 = description1;
    }

    public String getDescription2() {
        return Description2;
    }

    public void setDescription2(String description2) {
        Description2 = description2;
    }

    public String getDescription3() {
        return Description3;
    }

    public void setDescription3(String description3) {
        Description3 = description3;
    }

    public String getDescription4() {
        return Description4;
    }

    public void setDescription4(String description4) {
        Description4 = description4;
    }

    public String getDescription5() {
        return Description5;
    }

    public void setDescription5(String description5) {
        Description5 = description5;
    }

    public String getDescription6() {
        return Description6;
    }

    public void setDescription6(String description6) {
        Description6 = description6;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getDisabilityExt1() {
        return DisabilityExt1;
    }

    public void setDisabilityExt1(String disabilityExt1) {
        DisabilityExt1 = disabilityExt1;
    }

    public String getDisabilityExt2() {
        return DisabilityExt2;
    }

    public void setDisabilityExt2(String disabilityExt2) {
        DisabilityExt2 = disabilityExt2;
    }

    public String getDisabilityExt3() {
        return DisabilityExt3;
    }

    public void setDisabilityExt3(String disabilityExt3) {
        DisabilityExt3 = disabilityExt3;
    }

    public String getDisabilityExt4() {
        return DisabilityExt4;
    }

    public void setDisabilityExt4(String disabilityExt4) {
        DisabilityExt4 = disabilityExt4;
    }

    public String getDisabilityExt5() {
        return DisabilityExt5;
    }

    public void setDisabilityExt5(String disabilityExt5) {
        DisabilityExt5 = disabilityExt5;
    }

    public String getDisabilityExt6() {
        return DisabilityExt6;
    }

    public void setDisabilityExt6(String disabilityExt6) {
        DisabilityExt6 = disabilityExt6;
    }

    public String getDuration1() {
        return Duration1;
    }

    public void setDuration1(String duration1) {
        Duration1 = duration1;
    }

    public String getDuration2() {
        return Duration2;
    }

    public void setDuration2(String duration2) {
        Duration2 = duration2;
    }

    public String getDuration3() {
        return Duration3;
    }

    public void setDuration3(String duration3) {
        Duration3 = duration3;
    }

    public String getDuration4() {
        return Duration4;
    }

    public void setDuration4(String duration4) {
        Duration4 = duration4;
    }

    public String getDuration5() {
        return Duration5;
    }

    public void setDuration5(String duration5) {
        Duration5 = duration5;
    }

    public String getDuration6() {
        return Duration6;
    }

    public void setDuration6(String duration6) {
        Duration6 = duration6;
    }

    public String getEffectiveOnPH1() {
        return EffectiveOnPH1;
    }

    public void setEffectiveOnPH1(String effectiveOnPH1) {
        EffectiveOnPH1 = effectiveOnPH1;
    }

    public String getEffectiveOnPH2() {
        return EffectiveOnPH2;
    }

    public void setEffectiveOnPH2(String effectiveOnPH2) {
        EffectiveOnPH2 = effectiveOnPH2;
    }

    public String getEffectiveOnPH3() {
        return EffectiveOnPH3;
    }

    public void setEffectiveOnPH3(String effectiveOnPH3) {
        EffectiveOnPH3 = effectiveOnPH3;
    }

    public String getEffectiveOnPH4() {
        return EffectiveOnPH4;
    }

    public void setEffectiveOnPH4(String effectiveOnPH4) {
        EffectiveOnPH4 = effectiveOnPH4;
    }

    public String getEffectiveOnPH5() {
        return EffectiveOnPH5;
    }

    public void setEffectiveOnPH5(String effectiveOnPH5) {
        EffectiveOnPH5 = effectiveOnPH5;
    }

    public String getEffectiveOnPH6() {
        return EffectiveOnPH6;
    }

    public void setEffectiveOnPH6(String effectiveOnPH6) {
        EffectiveOnPH6 = effectiveOnPH6;
    }

    public String getEndTime1() {
        return EndTime1;
    }

    public void setEndTime1(String endTime1) {
        EndTime1 = endTime1;
    }

    public String getEndTime2() {
        return EndTime2;
    }

    public void setEndTime2(String endTime2) {
        EndTime2 = endTime2;
    }

    public String getEndTime3() {
        return EndTime3;
    }

    public void setEndTime3(String endTime3) {
        EndTime3 = endTime3;
    }

    public String getEndTime4() {
        return EndTime4;
    }

    public void setEndTime4(String endTime4) {
        EndTime4 = endTime4;
    }

    public String getEndTime5() {
        return EndTime5;
    }

    public void setEndTime5(String endTime5) {
        EndTime5 = endTime5;
    }

    public String getEndTime6() {
        return EndTime6;
    }

    public void setEndTime6(String endTime6) {
        EndTime6 = endTime6;
    }

    public String getFromDay1() {
        return FromDay1;
    }

    public void setFromDay1(String fromDay1) {
        FromDay1 = fromDay1;
    }

    public String getFromDay2() {
        return FromDay2;
    }

    public void setFromDay2(String fromDay2) {
        FromDay2 = fromDay2;
    }

    public String getFromDay3() {
        return FromDay3;
    }

    public void setFromDay3(String fromDay3) {
        FromDay3 = fromDay3;
    }

    public String getFromDay4() {
        return FromDay4;
    }

    public void setFromDay4(String fromDay4) {
        FromDay4 = fromDay4;
    }

    public String getFromDay5() {
        return FromDay5;
    }

    public void setFromDay5(String fromDay5) {
        FromDay5 = fromDay5;
    }

    public String getFromDay6() {
        return FromDay6;
    }

    public void setFromDay6(String fromDay6) {
        FromDay6 = fromDay6;
    }

    public String getStartTime1() {
        return StartTime1;
    }

    public void setStartTime1(String startTime1) {
        StartTime1 = startTime1;
    }

    public String getStartTime2() {
        return StartTime2;
    }

    public void setStartTime2(String startTime2) {
        StartTime2 = startTime2;
    }

    public String getStartTime3() {
        return StartTime3;
    }

    public void setStartTime3(String startTime3) {
        StartTime3 = startTime3;
    }

    public String getStartTime4() {
        return StartTime4;
    }

    public void setStartTime4(String startTime4) {
        StartTime4 = startTime4;
    }

    public String getStartTime5() {
        return StartTime5;
    }

    public void setStartTime5(String startTime5) {
        StartTime5 = startTime5;
    }

    public String getStartTime6() {
        return StartTime6;
    }

    public void setStartTime6(String startTime6) {
        StartTime6 = startTime6;
    }

    public String getToDay1() {
        return ToDay1;
    }

    public void setToDay1(String toDay1) {
        ToDay1 = toDay1;
    }

    public String getToDay2() {
        return ToDay2;
    }

    public void setToDay2(String toDay2) {
        ToDay2 = toDay2;
    }

    public String getToDay3() {
        return ToDay3;
    }

    public void setToDay3(String toDay3) {
        ToDay3 = toDay3;
    }

    public String getToDay4() {
        return ToDay4;
    }

    public void setToDay4(String toDay4) {
        ToDay4 = toDay4;
    }

    public String getToDay5() {
        return ToDay5;
    }

    public void setToDay5(String toDay5) {
        ToDay5 = toDay5;
    }

    public String getToDay6() {
        return ToDay6;
    }

    public void setToDay6(String toDay6) {
        ToDay6 = toDay6;
    }

    public String getTypeDesc1() {
        return TypeDesc1;
    }

    public void setTypeDesc1(String TypeDesc1) {
        TypeDesc1 = TypeDesc1;
    }

    public String getTypeDesc2() {
        return TypeDesc2;
    }

    public void setTypeDesc2(String TypeDesc2) {
        TypeDesc2 = TypeDesc2;
    }

    public String getTypeDesc3() {
        return TypeDesc3;
    }

    public void setTypeDesc3(String TypeDesc3) {
        TypeDesc3 = TypeDesc3;
    }

    public String getTypeDesc4() {
        return TypeDesc4;
    }

    public void setTypeDesc4(String TypeDesc4) {
        TypeDesc4 = TypeDesc4;
    }

    public String getTypeDesc5() {
        return TypeDesc5;
    }

    public void setTypeDesc5(String TypeDesc5) {
        TypeDesc5 = TypeDesc5;
    }

    public String getTypeDesc6() {
        return TypeDesc6;
    }

    public void setTypeDesc6(String TypeDesc6) {
        TypeDesc6 = TypeDesc6;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", status='" + status + '\'' +
                ", normalDuration=" + normalDuration +
                ", disDuration=" + disDuration +
                ", disableOnly='" + disableOnly + '\'' +
                ", needToPay='" + needToPay + '\'' +
                ", BayID='" + BayID + '\'' +
                ", Description1='" + Description1 + '\'' +
                ", Description2='" + Description2 + '\'' +
                ", Description3='" + Description3 + '\'' +
                ", Description4='" + Description4 + '\'' +
                ", Description5='" + Description5 + '\'' +
                ", Description6='" + Description6 + '\'' +
                ", DeviceID='" + DeviceID + '\'' +
                ", DisabilityExt1='" + DisabilityExt1 + '\'' +
                ", DisabilityExt2='" + DisabilityExt2 + '\'' +
                ", DisabilityExt3='" + DisabilityExt3 + '\'' +
                ", DisabilityExt4='" + DisabilityExt4 + '\'' +
                ", DisabilityExt5='" + DisabilityExt5 + '\'' +
                ", DisabilityExt6='" + DisabilityExt6 + '\'' +
                ", Duration1='" + Duration1 + '\'' +
                ", Duration2='" + Duration2 + '\'' +
                ", Duration3='" + Duration3 + '\'' +
                ", Duration4='" + Duration4 + '\'' +
                ", Duration5='" + Duration5 + '\'' +
                ", Duration6='" + Duration6 + '\'' +
                ", EffectiveOnPH1='" + EffectiveOnPH1 + '\'' +
                ", EffectiveOnPH2='" + EffectiveOnPH2 + '\'' +
                ", EffectiveOnPH3='" + EffectiveOnPH3 + '\'' +
                ", EffectiveOnPH4='" + EffectiveOnPH4 + '\'' +
                ", EffectiveOnPH5='" + EffectiveOnPH5 + '\'' +
                ", EffectiveOnPH6='" + EffectiveOnPH6 + '\'' +
                ", EndTime1='" + EndTime1 + '\'' +
                ", EndTime2='" + EndTime2 + '\'' +
                ", EndTime3='" + EndTime3 + '\'' +
                ", EndTime4='" + EndTime4 + '\'' +
                ", EndTime5='" + EndTime5 + '\'' +
                ", EndTime6='" + EndTime6 + '\'' +
                ", FromDay1='" + FromDay1 + '\'' +
                ", FromDay2='" + FromDay2 + '\'' +
                ", FromDay3='" + FromDay3 + '\'' +
                ", FromDay4='" + FromDay4 + '\'' +
                ", FromDay5='" + FromDay5 + '\'' +
                ", FromDay6='" + FromDay6 + '\'' +
                ", StartTime1='" + StartTime1 + '\'' +
                ", StartTime2='" + StartTime2 + '\'' +
                ", StartTime3='" + StartTime3 + '\'' +
                ", StartTime4='" + StartTime4 + '\'' +
                ", StartTime5='" + StartTime5 + '\'' +
                ", StartTime6='" + StartTime6 + '\'' +
                ", ToDay1='" + ToDay1 + '\'' +
                ", ToDay2='" + ToDay2 + '\'' +
                ", ToDay3='" + ToDay3 + '\'' +
                ", ToDay4='" + ToDay4 + '\'' +
                ", ToDay5='" + ToDay5 + '\'' +
                ", ToDay6='" + ToDay6 + '\'' +
                ", TypeDesc1='" + TypeDesc1 + '\'' +
                ", TypeDesc2='" + TypeDesc2 + '\'' +
                ", TypeDesc3='" + TypeDesc3 + '\'' +
                ", TypeDesc4='" + TypeDesc4 + '\'' +
                ", TypeDesc5='" + TypeDesc5 + '\'' +
                ", TypeDesc6='" + TypeDesc6 + '\'' +
                '}';
    }

    // ======= check duration based on current time and dayOfWeek
    public void checkSpot() {
        final String OLD_FORMAT = "hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        int dayOfWeek = Calendar.DAY_OF_WEEK;
        Date currentTime = Calendar.getInstance().getTime();

        if (this.Description1 != ""){
            if (FromDay1.length()==0){
                FromDay1 = "0";
            }
            if (ToDay1.length()==0){
                ToDay1 = "0";
            }
            if (Duration1.length()==0){
                Duration1 = "0";
            }
            if (DisabilityExt1.length()==0){
                DisabilityExt1 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay1) && dayOfWeek <= Integer.parseInt(ToDay1)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime1);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime1);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc1.contains("Meter") || this.TypeDesc1.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.Description1.contains("DIS")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration1);
                    this.disDuration = Integer.parseInt(DisabilityExt1);
                    return;
                }

            }
        }

        if (this.Description2 != ""){
            if (FromDay2.length()==0){
                FromDay2 = "0";
            }
            if (ToDay2.length()==0){
                ToDay2 = "0";
            }
            if (Duration2.length()==0){
                Duration2 = "0";
            }
            if (DisabilityExt2.length()==0){
                DisabilityExt2 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay2) && dayOfWeek <= Integer.parseInt(ToDay2)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime2);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime2);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc2.contains("Meter") || this.TypeDesc2.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.TypeDesc2.contains("Disabled Only")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration2);
                    this.disDuration = Integer.parseInt(DisabilityExt2);
                    return;
                }

            }
        }

        if (this.Description3 != ""){
            if (FromDay3.length()==0){
                FromDay3 = "0";
            }
            if (ToDay3.length()==0){
                ToDay3 = "0";
            }
            if (Duration3.length()==0){
                Duration3 = "0";
            }
            if (DisabilityExt3.length()==0){
                DisabilityExt3 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay3) && dayOfWeek <= Integer.parseInt(ToDay3)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime3);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime3);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc3.contains("Meter") || this.TypeDesc3.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.TypeDesc3.contains("Disabled Only")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration3);
                    this.disDuration = Integer.parseInt(DisabilityExt3);
                    return;
                }

            }
        }

        if (this.Description4 != ""){
            if (FromDay4.length()==0){
                FromDay4 = "0";
            }
            if (ToDay4.length()==0){
                ToDay4 = "0";
            }
            if (Duration4.length()==0){
                Duration4 = "0";
            }
            if (DisabilityExt4.length()==0){
                DisabilityExt4 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay4) && dayOfWeek <= Integer.parseInt(ToDay4)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime4);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime4);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc4.contains("Meter") || this.TypeDesc4.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.TypeDesc4.contains("Disabled Only")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration4);
                    this.disDuration = Integer.parseInt(DisabilityExt4);
                    return;
                }

            }
        }


        if (this.Description5 != ""){
            if (FromDay5.length()==0){
                FromDay5 = "0";
            }
            if (ToDay5.length()==0){
                ToDay5 = "0";
            }
            if (Duration5.length()==0){
                Duration5 = "0";
            }
            if (DisabilityExt5.length()==0){
                DisabilityExt5 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay5) && dayOfWeek <= Integer.parseInt(ToDay5)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime5);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime5);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc5.contains("Meter") || this.TypeDesc5.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.TypeDesc5.contains("Disabled Only")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration5);
                    this.disDuration = Integer.parseInt(DisabilityExt5);
                    return;
                }

            }
        }

        if (this.Description6 != ""){
            if (FromDay6.length()==0){
                FromDay6 = "0";
            }
            if (ToDay6.length()==0){
                ToDay6 = "0";
            }
            if (Duration6.length()==0){
                Duration6 = "0";
            }
            if (DisabilityExt6.length()==0){
                DisabilityExt6 = "0";
            }
            if (dayOfWeek >= Integer.parseInt(FromDay6) && dayOfWeek <= Integer.parseInt(ToDay6)){
                Date startTimeTime = null;
                try {
                    startTimeTime = sdf.parse(StartTime6);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                Date endTimeTime = null;
                try {
                    endTimeTime = sdf.parse(EndTime6);
                } catch (ParseException e) {
                    Log.d("ParkingSpotChekcingTime", "Convert time error");
                    e.printStackTrace();
                }

                if (currentTime.before(endTimeTime) && currentTime.after(startTimeTime)){
                    Log.d("ParkingSpotChekcingTime", "Find it");
                    if (this.TypeDesc6.contains("Meter") || this.TypeDesc6.contains("Ticket")){
                        this.needToPay = "yes";
                    }else{
                        this.needToPay = "no";
                    }

                    if (this.TypeDesc6.contains("Disabled Only")){
                        this.disableOnly = "yes";
                    }else{
                        this.disableOnly = "no";
                    }
                    this.normalDuration = Integer.parseInt(Duration6);
                    this.disDuration = Integer.parseInt(DisabilityExt6);
                    return;
                }

            }
        }

        this.needToPay = "no";
        this.disableOnly = "no";
        this.normalDuration = 121;
        this.disDuration = 121;
        return;
        
        
    }

    public boolean chechWithSampleSpot(ParkingSpot sample){
        if (sample.getStatus() != ""){
            if (!sample.getStatus().equals(this.status)){
                return false;
            }
        }

        if (sample.getNeedToPay() != ""){
            if (!sample.getNeedToPay().equals(this.needToPay)){
                return false;
            }
        }

        if (sample.getNormalDuration() != 0){
            if (sample.getNormalDuration() >= this.normalDuration){
                return false;
            }
        }

        if (sample.getDisableOnly() != ""){
            if (!sample.getDisableOnly().equals(this.disableOnly)){
                return false;
            }
        }

        return true;
    }


}