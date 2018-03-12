package com.example.alex.raidcall;

/**
 * Created by Alex on 6/20/2017.
 */

public class Raid {

    private String Boss;
    private String Date;
    private String Time;
    private String Image;
    private String IGN;





    public Raid(){

    }

    public Raid(String boss, String date, String time, String image, String ign) {
        Boss = boss;
        Date = date;
        Time = time;
        Image = image;
        IGN = ign;
    }



    public String getBoss() {
        return Boss;
    }

    public void setBoss(String boss) {
        Boss = boss;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getIGN() {return IGN;}

    public void setIGN(String IGN) { this.IGN = IGN; }


}
