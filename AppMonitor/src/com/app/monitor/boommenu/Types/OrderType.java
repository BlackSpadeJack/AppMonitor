package com.app.monitor.boommenu.Types;



public enum OrderType {

    DEFAULT(0),
    REVERSE(1),
    RANDOM(2);

    int type;

    OrderType(int type) {
        this.type = type;
    }
}
