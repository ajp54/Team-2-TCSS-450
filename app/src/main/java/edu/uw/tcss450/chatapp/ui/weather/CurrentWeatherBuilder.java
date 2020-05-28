package edu.uw.tcss450.chatapp.ui.weather;

import java.io.Serializable;

public class CurrentWeatherBuilder implements Serializable {

    private final String mTemp_F;
    private final String mWindSpeedMiles;
    private final String mHumidity;
    private final String mPrecipMM;


    public static class Builder {
        private final String mTemp_F;
        private final String mWindSpeedMiles;
        private final String mHumidity;
        private final String mPrecipMM;

        public Builder(String temp_F, String windSpeedMiles, String humidity, String precipMM) {
            this.mTemp_F = temp_F;
            this.mWindSpeedMiles = windSpeedMiles;
            this.mHumidity = humidity;
            this.mPrecipMM = precipMM;
        }

        public CurrentWeatherBuilder build() {
            return new CurrentWeatherBuilder(this);
        }
    }

    private CurrentWeatherBuilder(final Builder builder) {
        this.mTemp_F = builder.mTemp_F;
        this.mWindSpeedMiles = builder.mWindSpeedMiles;
        this.mHumidity = builder.mHumidity;
        this.mPrecipMM = builder.mPrecipMM;
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
}
