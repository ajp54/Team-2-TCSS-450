package edu.uw.tcss450.chatapp.ui.weather;

public class Day {

    String mDay;
    String mTemp;
    public Day(String day, String temp) {
        mDay = day;
        mTemp = temp;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public String getmTemp() {
        return mTemp;
    }

    public void setmTemp(String mTemp) {
        this.mTemp = mTemp;
    }
}
