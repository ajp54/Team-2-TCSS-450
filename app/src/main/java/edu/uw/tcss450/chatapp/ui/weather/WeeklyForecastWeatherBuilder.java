package edu.uw.tcss450.chatapp.ui.weather;

import java.io.Serializable;

public class WeeklyForecastWeatherBuilder implements Serializable {
    private final String mDate;
    private final String mAvgTempF;


    public static class Builder {
        private final String mDate;
        private final String mAvgTempF;

        public Builder(String date, String avgTempF) {
            this.mDate = date;
            this.mAvgTempF = avgTempF + " F";
        }

        public WeeklyForecastWeatherBuilder build() {
            return new WeeklyForecastWeatherBuilder(this);
        }
    }

    private WeeklyForecastWeatherBuilder(final Builder builder) {
        this.mDate = builder.mDate;
        this.mAvgTempF = builder.mAvgTempF;
    }

    public String getDate() {
        return mDate;
    }

    public String getAvgTempF() {
        return mAvgTempF;
    }
}
