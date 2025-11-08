package com.example.deviceapi.model;

import java.util.ArrayList;
import java.util.List;

public class NetworkNode {
    private String macAddress;
    private DeviceType deviceType;
    private List<NetworkNode> children = new ArrayList<>();

    public NetworkNode() {
    }

    public NetworkNode(String macAddress, DeviceType deviceType) {
        this.macAddress = macAddress;
        this.deviceType = deviceType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public List<NetworkNode> getChildren() {
        return children;
    }
}
