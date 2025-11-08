package com.example.deviceapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Device {
    private DeviceType deviceType;

    @NotBlank
    @Size(min = 17, max = 17)
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress must be in format XX:XX:XX:XX:XX:XX"
    )
    private String macAddress;

    @Pattern(
            regexp = "^$|^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "uplinkMacAddress must be empty or XX:XX:XX:XX:XX:XX"
    )
    private String uplinkMacAddress;

    public Device() {
    }

    public Device(DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        this.deviceType = deviceType;
        this.macAddress = macAddress;
        this.uplinkMacAddress = uplinkMacAddress == null ? "" : uplinkMacAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getUplinkMacAddress() {
        return uplinkMacAddress == null ? "" : uplinkMacAddress;
    }

    public void setUplinkMacAddress(String uplinkMacAddress) {
        this.uplinkMacAddress = uplinkMacAddress;
    }
}
