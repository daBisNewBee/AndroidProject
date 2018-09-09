package com.exa.eventbus;

/**
 * Created by user on 2018/9/8.
 */

public class EventBean {

    private String info;

    public EventBean(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "info='" + info + '\'' +
                '}';
    }
}
