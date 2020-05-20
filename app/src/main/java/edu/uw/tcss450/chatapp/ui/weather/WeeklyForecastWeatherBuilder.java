package edu.uw.tcss450.chatapp.ui.weather;

import java.io.Serializable;

public class WeeklyForecastWeatherBuilder implements Serializable {
    private static String mDate;
    private static String mAvgTempF;


    public static class Builder {
        private final String mDate;
        private final String mAvgTempF;

        public Builder(String date, String avgTempF) {
            this.mDate = date;
            this.mAvgTempF = avgTempF;
        }

        public WeeklyForecastWeatherBuilder build() {
            return new WeeklyForecastWeatherBuilder(this);
        }
    }

    private WeeklyForecastWeatherBuilder(final Builder builder) {
        this.mDate = builder.mDate;
        this.mAvgTempF = builder.mAvgTempF;
    }

    public static String getDate() {
        return mDate;
    }

    public static String getAvgTempF() {
        return mAvgTempF;
    }
}
