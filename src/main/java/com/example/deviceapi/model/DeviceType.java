package com.example.deviceapi.model;

public enum DeviceType {
    GATEWAY, SWITCH, ACCESS_POINT;

    public int sortOrder() {
        return switch (this) {
            case GATEWAY -> 0;
            case SWITCH -> 1;
            case ACCESS_POINT -> 2;
        };
    }
}
