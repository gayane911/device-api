package com.example.deviceapi.repo;

import com.example.deviceapi.model.Device;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DeviceRepository {
    private final Map<String, Device> byMac = new ConcurrentHashMap<>();

    public Optional<Device> findByMac(String mac) {
        return Optional.ofNullable(byMac.get(mac));
    }

    public Device save(Device device) {
        byMac.put(device.getMacAddress(), device);
        return device;
    }

    public List<Device> findAll() {
        return new ArrayList<>(byMac.values());
    }
}
