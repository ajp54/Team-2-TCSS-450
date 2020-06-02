package edu.uw.tcss450.chatapp.ui.weather;

import java.io.Serializable;

public class DailyForecastWeatherBuilder implements Serializable {
    private final String mAvgTempF;
    private final String mIconUrl;


    public static class Builder {
        private final String mAvgTempF;
        private final String mIconUrl;

        public Builder(String avgtempF, String iconUrl) {
            this.mAvgTempF = avgtempF + " F";
            this.mIconUrl = iconUrl;
        }

        public DailyForecastWeatherBuilder build() {

            return new DailyForecastWeatherBuilder(this);
        }
    }

    private DailyForecastWeatherBuilder(final Builder builder) {
        this.mAvgTempF = builder.mAvgTempF;
        this.mIconUrl = builder.mIconUrl;
    }


    public String getAvgTempF() {
        return mAvgTempF;
    }

    public String getIconUrl() {
        return mIconUrl;
    }
}
