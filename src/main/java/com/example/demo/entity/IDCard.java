package com.example.demo.entity;

import javax.lang.model.element.NestingKind;

/**
 * @author ly
 * @since 2021/5/8
 */
public class IDCard {

    private String name;
    private Character sex;
    private String nation;
    private Integer year;
    private Integer month;
    private Integer day;
    private String address;
    private String idNumber;

    public IDCard() {
        super();
    }

    public IDCard(String name, Character sex, String nation, Integer year, Integer month, Integer day, String address, String idNumber) {
        this.name = name;
        this.sex = sex;
        this.nation = nation;
        this.year = year;
        this.month = month;
        this.day = day;
        this.address = address;
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation == null ? null : nation.trim();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }
}
