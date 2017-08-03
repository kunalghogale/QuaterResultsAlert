package com.kunal.vo;

/**
 * Created by kunal on 7/27/17.
 */
public class StocksVO {
    String timeOfDay;
    String companyName;
    String marketCap;
    String epsForecast;
    String symbol;
    double cap;

    public StocksVO(String timeOfDay, String companyName, String marketCap, String epsForecast) {
        this.timeOfDay = timeOfDay;
        this.companyName = companyName;
        this.marketCap = marketCap;
        this.epsForecast = epsForecast;
        this.symbol = companyName.substring(companyName.indexOf("(") + 1, companyName.indexOf(")"));
        String temp = marketCap.replace("$", "");
        if (temp.contains("M")) {
            cap = Float.parseFloat(temp.replace("M", "")) * 1000000;
        } else if (temp.contains("B")) {
            cap = Float.parseFloat(temp.replace("B", "")) * 1000000000;
        } else {
            cap = -1;
        }
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getCap() {
        return cap;
    }

    public void setCap(double cap) {
        this.cap = cap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StocksVO stocksVO = (StocksVO) o;

        if (Double.compare(stocksVO.cap, cap) != 0) return false;
        if (timeOfDay != null ? !timeOfDay.equals(stocksVO.timeOfDay) : stocksVO.timeOfDay != null) return false;
        if (companyName != null ? !companyName.equals(stocksVO.companyName) : stocksVO.companyName != null) return false;
        if (marketCap != null ? !marketCap.equals(stocksVO.marketCap) : stocksVO.marketCap != null) return false;
        if (epsForecast != null ? !epsForecast.equals(stocksVO.epsForecast) : stocksVO.epsForecast != null) return false;
        return symbol != null ? symbol.equals(stocksVO.symbol) : stocksVO.symbol == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = timeOfDay != null ? timeOfDay.hashCode() : 0;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (marketCap != null ? marketCap.hashCode() : 0);
        result = 31 * result + (epsForecast != null ? epsForecast.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        temp = Double.doubleToLongBits(cap);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "StocksVO{" +
            "timeOfDay='" + timeOfDay + '\'' +
            ", companyName='" + companyName + '\'' +
            ", marketCap='" + marketCap + '\'' +
            ", epsForecast='" + epsForecast + '\'' +
            ", symbol='" + symbol + '\'' +
            ", cap=" + cap +
            '}';
    }
}
