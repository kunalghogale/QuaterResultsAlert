package com.kunal.vo;

/**
 * Created by kunal on 7/27/17.
 */
public class StocksVO {
    String timeOfDay;
    String companyName;
    String marketCap;
    String epsForecast;

    public StocksVO(String timeOfDay, String companyName, String marketCap, String epsForecast) {
        this.timeOfDay = timeOfDay;
        this.companyName = companyName;
        this.marketCap = marketCap;
        this.epsForecast = epsForecast;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getEpsForecast() {
        return epsForecast;
    }

    public void setEpsForecast(String epsForecast) {
        this.epsForecast = epsForecast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StocksVO stocksVO = (StocksVO) o;

        if (timeOfDay != null ? !timeOfDay.equals(stocksVO.timeOfDay) : stocksVO.timeOfDay != null) return false;
        if (companyName != null ? !companyName.equals(stocksVO.companyName) : stocksVO.companyName != null)
            return false;
        if (marketCap != null ? !marketCap.equals(stocksVO.marketCap) : stocksVO.marketCap != null) return false;
        return epsForecast != null ? epsForecast.equals(stocksVO.epsForecast) : stocksVO.epsForecast == null;
    }

    @Override
    public int hashCode() {
        int result = timeOfDay != null ? timeOfDay.hashCode() : 0;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (marketCap != null ? marketCap.hashCode() : 0);
        result = 31 * result + (epsForecast != null ? epsForecast.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StocksVO{" +
                "timeOfDay='" + timeOfDay + '\'' +
                ", companyName='" + companyName + '\'' +
                ", marketCap='" + marketCap + '\'' +
                ", epsForecast='" + epsForecast + '\'' +
                '}';
    }
}
