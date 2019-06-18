package com.ioter.eastsoft.data.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class EpcModel {
    @Id(autoincrement = true)
    private Long id;
    private String card;
    private String name;
    private String sex;
    private String date;
    @Generated(hash = 381351099)
    public EpcModel(Long id, String card, String name, String sex, String date) {
        this.id = id;
        this.card = card;
        this.name = name;
        this.sex = sex;
        this.date = date;
    }
    @Generated(hash = 2138003262)
    public EpcModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCard() {
        return this.card;
    }
    public void setCard(String card) {
        this.card = card;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
