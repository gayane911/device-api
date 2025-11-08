package com.example.deviceapi.controller;

import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.NetworkNode;
import com.example.deviceapi.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeviceController {
    private final DeviceService service;

    public DeviceController(DeviceService service) { this.service = service; }

    @PostMapping("/devices")
    @ResponseStatus(HttpStatus.CREATED)
    public Device register(@Valid @RequestBody Device device) {
        return service.register(device);
    }

    @GetMapping("/devices")
    public List<Device> list() { return service.allSortedByType(); }

    @GetMapping("/devices/{mac}")
    public Device get(@PathVariable("mac") String mac) { return service.getByMac(mac); }

    @GetMapping("/topology")
    public List<NetworkNode> networkDeviceTopology() { return service.fullNetworkTopology(); }

    @GetMapping("/topology/{mac}")
    public NetworkNode networkDeviceTopologyFrom(@PathVariable("mac") String mac) { return service.networkTopologyFrom(mac); }
}
