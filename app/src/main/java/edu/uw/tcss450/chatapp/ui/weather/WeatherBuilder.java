package edu.uw.tcss450.chatapp.ui.weather;

import java.io.Serializable;

public class WeatherBuilder implements Serializable {

    private final String mTemp_F;
    private final String mWindSpeedMiles;
    private final String mHumidity;
    private final String mPrecipMM;
    private final String mDate;
    private final String mAvgTempF;

    public static class Builder {
        private final String mTemp_F;
        private final String mWindSpeedMiles;
        private final String mHumidity;
        private final String mPrecipMM;
        private final String mDate;
        private final String mAvgTempF;

        public Builder(String temp_F, String windSpeedMiles, String humidity,
                       String precipMM, String date, String avgTempF) {
            this.mTemp_F = temp_F;
            this.mWindSpeedMiles = windSpeedMiles;
            this.mHumidity = humidity;
            this.mPrecipMM = precipMM;
            this.mDate = date;
            this.mAvgTempF = avgTempF;
        }

        public WeatherBuilder build() {
            return new WeatherBuilder(this);
        }
    }

    private WeatherBuilder(final Builder builder) {
        this.mTemp_F = builder.mTemp_F;
        this.mWindSpeedMiles = builder.mWindSpeedMiles;
        this.mHumidity = builder.mHumidity;
        this.mPrecipMM = builder.mPrecipMM;
        this.mDate = builder.mDate;
        this.mAvgTempF = builder.mAvgTempF;
    }

    public String getTemp_F() {
        return mTemp_F;
    }

    public String getWindSpeedMiles() {
        return mWindSpeedMiles;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public String getPrecipMM() {
        return mPrecipMM;
    }

    public String getDate() {
        return mDate;
    }

    public String getAvgTempF() {
        return mAvgTempF;
    }
}
