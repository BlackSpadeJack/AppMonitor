package com.app.monitor.boommenu.Types;


public enum StateType {

    CLOSED(0),
    OPENING(1),
    OPEN(2),
    CLOSING(3);

    int type;

    StateType(int type) {
        this.type = type;
    }
}
