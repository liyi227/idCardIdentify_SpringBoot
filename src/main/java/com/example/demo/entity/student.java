package com.example.demo.entity;

/**
 * @author ly
 * @since 2021/5/8
 */
public class student {
    private int Id;
    private String name;
    private int age;
    private String gender;

    public student(){

    }
    public student(String name, int age, String gender){
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public student(int id, String name, int age, String gender) {
        Id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }


    @Override
    public String toString() {
        return "student{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}