package com.chinatelecom.vo;

import java.io.Serializable;

/**
 * Description: shiroProject
 * Created by leizhaoyuan on 20/2/18 上午12:31
 */
public class Member implements Serializable {
    private String mid;
    private String name;
    private String password;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Member{" +
                "mid='" + mid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
