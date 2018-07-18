package com.example.vicky.passmanager;

public class User {

    public Double age;
    public String sex;

    public User(){}

    public User(Double age, String sex){
        this.age=age;
        this.sex=sex;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Double getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }
}
