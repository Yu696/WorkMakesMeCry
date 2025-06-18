package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AQIData {
//    "AQI反馈数据编号": "001",
//            "所在省区域": "广东省",
//            "所在市区域": "广州市",
//            "详细地址":"天河区XX路XX号",
//            "预估AQI等级": "良",
//            "反馈日期":"2025-06-10",
//            "反馈信息详情": "空气质量良好，无明显污染",
//            "反馈者姓名": "张三"
    @JsonProperty("num")
    private String num;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("detailedAddress")
    private String detailedAddress;
    @JsonProperty("AQILevel")
    private String AQILevel;
    @JsonProperty("date")
    private String date;
    @JsonProperty("detailedInfo")
    private String detailedInfo;
    @JsonProperty("publisher")
    private String publisher;
    @JsonProperty("gridder")
    private String gridder;
    @JsonProperty("state")
    private String state;
    // 添加实测数据字段
    @JsonProperty("so2")
    private Double so2;
    @JsonProperty("co")
    private Double co;
    @JsonProperty("pm25")
    private Double pm25;
    @JsonProperty("so2Level")
    private String so2Level;
    @JsonProperty("coLevel")
    private String coLevel;
    @JsonProperty("pm25Level")
    private String pm25Level;
    @JsonProperty("finalLevel")
    private String finalLevel;
    @JsonProperty("finalPollution")
    private String finalPollution;
    public AQIData(String num, String province, String detailedAddress, String city, String AQILevel, String date, String detailedInfo, String publisher) {
        this.num = num;
        this.province = province;
        this.detailedAddress = detailedAddress;
        this.city = city;
        this.AQILevel = AQILevel;
        this.date = date;
        this.detailedInfo = detailedInfo;
        this.publisher = publisher;
        this.gridder=null;
        this.state="未检阅";
    }

    public AQIData(String num, String province, String city, String detailedAddress, String AQILevel, String date, String detailedInfo, String publisher, String gridder, String state) {
        this.num = num;
        this.province = province;
        this.city = city;
        this.detailedAddress = detailedAddress;
        this.AQILevel = AQILevel;
        this.date = date;
        this.detailedInfo = detailedInfo;
        this.publisher = publisher;
        this.gridder = gridder;
        this.state = state;
        this.so2 = null;
        this.co = null;
        this.pm25 = null;
        this.so2Level = null;
        this.coLevel = null;
        this.pm25Level = null;
        this.finalLevel = null;
        this.finalPollution = null;
    }

    public AQIData() {
    }

    public String getNum() {
        return num;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGridder() {
        return gridder;
    }

    public void setGridder(String gridder) {
        this.gridder = gridder;
    }

    public String getAQILevel() {
        return AQILevel;
    }

    public void setAQILevel(String AQILevel) {
        this.AQILevel = AQILevel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(String detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public String getPublisher() {
        return publisher;
    }


    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // 添加实测数据的getter和setter方法
    public Double getSo2() {
        return so2;
    }

    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    public Double getCo() {
        return co;
    }

    public void setCo(Double co) {
        this.co = co;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public String getSo2Level() {
        return so2Level;
    }

    public void setSo2Level(String so2Level) {
        this.so2Level = so2Level;
    }

    public String getCoLevel() {
        return coLevel;
    }

    public void setCoLevel(String coLevel) {
        this.coLevel = coLevel;
    }

    public String getPm25Level() {
        return pm25Level;
    }

    public void setPm25Level(String pm25Level) {
        this.pm25Level = pm25Level;
    }

    public String getFinalLevel() {
        return finalLevel;
    }

    public void setFinalLevel(String finalLevel) {
        this.finalLevel = finalLevel;
    }

    public String getFinalPollution() {
        return finalPollution;
    }

    public void setFinalPollution(String finalPollution) {
        this.finalPollution = finalPollution;
    }


    @Override
    public String toString() {
        return "AQI反馈数据编号:" + this.num + "\n" +
                "所在省区域:" + this.province + "\n" +
                "所在市区域:" + this.city + "\n" +
                "详细地址:" + this.detailedAddress + "\n" +
                "预估AQI等级:" + this.AQILevel + "\n" +
                "反馈日期:" + this.date + "\n" +
                "反馈信息详情:" + this.detailedInfo + "\n" +
                "反馈者姓名:" + this.publisher + "\n" +
                "分配给的网格员:" + (this.gridder != null ? this.gridder : "未分配") + "\n" +
                "报告状态:" + this.state + "\n" +
                "SO2浓度:" + (this.so2 != null ? this.so2 + " ug/m3" : "未测量") + "\n" +
                "SO2等级:" + (this.so2Level != null ? this.so2Level : "未评定") + "\n" +
                "CO浓度:" + (this.co != null ? this.co + " ug/m3" : "未测量") + "\n" +
                "CO等级:" + (this.coLevel != null ? this.coLevel : "未评定") + "\n" +
                "PM2.5浓度:" + (this.pm25 != null ? this.pm25 + " ug/m3" : "未测量") + "\n" +
                "PM2.5等级:" + (this.pm25Level != null ? this.pm25Level : "未评定") + "\n" +
                "最终AQI等级:" + (this.finalLevel != null ? this.finalLevel : "未评定") + "\n" +
                "最终污染程度:" + (this.finalPollution != null ? this.finalPollution : "未评定");
    }

}
