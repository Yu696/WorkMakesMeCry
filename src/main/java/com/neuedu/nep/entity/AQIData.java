package com.neuedu.nep.entity;
public class AQIData {
//    "AQI反馈数据编号": "001",
//            "所在省区域": "广东省",
//            "所在市区域": "广州市",
//            "详细地址":"天河区XX路XX号",
//            "预估AQI等级": "良",
//            "反馈日期":"2025-06-10",
//            "反馈信息详情": "空气质量良好，无明显污染",
//            "反馈者姓名": "张三"
    private String num;
    private String province;
    private String city;
    private String detailedAddress;
    private String AQILevel;
    private String date;
    private String detailedInfo;
    private String publisher;

    public AQIData(String num, String province, String detailedAddress, String city, String AQILevel, String date, String detailedInfo, String publisher) {
        this.num = num;
        this.province = province;
        this.detailedAddress = detailedAddress;
        this.city = city;
        this.AQILevel = AQILevel;
        this.date = date;
        this.detailedInfo = detailedInfo;
        this.publisher = publisher;
    }

    public AQIData() {
    }

    public String getNum() {
        return num;
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

    @Override
    public String toString() {
        return "AQI反馈数据编号: " +this.num+"\n"+
                "所在省区域: "+this.province+"\n" +
                "所在市区域: "+this.city+"\n" +
                "详细地址: "+this.detailedAddress+"\n" +
                "预估AQI等级: "+this.AQILevel+"\n" +
                "反馈日期: "+this.date+"\n" +
                "反馈信息详情: "+this.detailedInfo+"\n" +
                "反馈者姓名: "+this.publisher+"\n";
    }
}
