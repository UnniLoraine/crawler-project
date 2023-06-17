package com.edkai.modle;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class User {
    @ExcelProperty("email")
    private String email;
    @ExcelProperty("location")
    private String location;
    @ExcelProperty("Brand Mention")
    private String brandMentions;

    @ExcelProperty({"ins", "ins_name"})
    private String insName;
    @ExcelProperty({"ins", "ins_subscribers"})
    private String insSubscribers;
//    @ExcelProperty({"ins", "ins_avg_views"})
//    private String insAvgViews;
    @ExcelProperty({"ins", "ins_avg_likes"})
    private String insAvgLikes;
    @ExcelProperty({"ins", "ins_avg_engageRate"})
    private String insAvgEngageRate;
    @ExcelProperty({"ins", "ins_cities"})
    private String insCities;
    @ExcelProperty({"ins", "ins_countries"})
    private String insCountries;
    @ExcelProperty({"ins", "ins_ages"})
    private String insAges;
    @ExcelProperty({"ins", "ins_gender"})
    private String insGender;
    @ExcelProperty({"ins", "ins_suggest_price"})
    private String insSugPrice;


    @ExcelProperty({"youtube", "name"})
    private String youName;
    @ExcelProperty({"youtube", "subscribers"})
    private String youSubscribers;
    @ExcelProperty({"youtube", "avg_views"})
    private String youAvgViews;
    @ExcelProperty({"youtube", "avg_likes"})
    private String youAvgLikes;
    @ExcelProperty({"youtube", "avg_engageRate"})
    private String youAvgEngageRate;
    @ExcelProperty({"youtube", "suggest price"})
    private String youSugPrice;


    @ExcelProperty({"pinterest", "name"})
    private String pinName;
    @ExcelProperty({"pinterest", "subscribers"})
    private String pinSubscribers;
//    @ExcelProperty({"pinterest", "avg_views"})
//    private String pinAvgViews;
    @ExcelProperty({"pinterest", "avg_likes"})
    private String pinAvgLikes;
    @ExcelProperty({"pinterest", "avg_engageRate"})
    private String pinAvgEngageRate;
//    @ExcelProperty({"pinterest", "suggest price"})
//    private String pinSugPrice;

    @ExcelProperty({"tiktok", "name"})
    private String tikName;
    @ExcelProperty({"tiktok", "subscribers"})
    private String tikSubscribers;
    @ExcelProperty({"tiktok", "avg_views"})
    private String tikAvgViews;
    @ExcelProperty({"tiktok", "avg_likes"})
    private String tikAvgLikes;
    @ExcelProperty({"tiktok", "avg_engageRate"})
    private String tikAvgEngageRate;
//    @ExcelProperty({"tiktok", "cities"})
//    private String tikCities;
    @ExcelProperty({"tiktok", "countries"})
    private String tikCountries;
    @ExcelProperty({"tiktok", "ages"})
    private String tikAges;
    @ExcelProperty({"tiktok", "gender"})
    private String tikGender;
//    @ExcelProperty({"tiktok", "suggest price"})
//    private String tikSugPrice;


}





