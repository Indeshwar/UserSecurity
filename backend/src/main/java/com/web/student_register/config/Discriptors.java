package com.web.student_register.config;

public class Discriptors {
    private String key;
    private String value;
    private RateLimit rateLimit;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public static class RateLimit{
        private String unit;
        private long timeFrame;
        private int requestsPerUnit;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public long getTimeFrame() {
            return timeFrame;
        }

        public void setTimeFrame(long timeFrame) {
            this.timeFrame = timeFrame;
        }

        public int getRequestsPerUnit() {
            return requestsPerUnit;
        }

        public void setRequestsPerUnit(int requestsPerUnit) {
            this.requestsPerUnit = requestsPerUnit;
        }
    }
}
