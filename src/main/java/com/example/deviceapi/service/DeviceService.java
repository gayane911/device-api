package com.example.deviceapi.service;

import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.NetworkNode;
import com.example.deviceapi.repo.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository repo;

    public DeviceService(DeviceRepository repo) {
        this.repo = repo;
    }

    public Device register(Device device) {
        String uplink = device.getUplinkMacAddress();
        if (uplink != null) uplink = uplink.trim();
        if (device.getMacAddress().equalsIgnoreCase(uplink)) {
            throw new IllegalArgumentException("Device cannot uplink to itself");
        }
        device.setUplinkMacAddress(uplink == null ? "" : uplink);
        return repo.save(device);
    }

    public List<Device> allSortedByType() {
        return repo.findAll().stream()
                .sorted(Comparator
                        .comparing((Device d) -> d.getDeviceType().sortOrder()))
                .toList();
    }

    public Device getByMac(String mac) {
        return repo.findByMac(mac).orElseThrow(() -> new NoSuchElementException("Device not found: " + mac));
    }

    public List<NetworkNode> fullNetworkTopology() {
        return buildNetwork(null);
    }

    public NetworkNode networkTopologyFrom(String mac) {
        Map<String, NetworkNode> index = indexNodes();
        NetworkNode root = index.get(mac);
        if (root == null) throw new NoSuchElementException("Device not found: " + mac);
        return root;
    }

    private List<NetworkNode> buildNetwork(String rootMac) {
        Map<String, NetworkNode> index = indexNodes();
        if (rootMac != null) {
            NetworkNode root = index.get(rootMac);
            if (root == null) {
                throw new NoSuchElementException("Device not found: " + rootMac);
            }
            return List.of(root);
        }

        List<NetworkNode> roots = repo.findAll().stream()
                .filter(d -> {
                    String uplink = d.getUplinkMacAddress();
                    return uplink == null || uplink.isBlank() || !index.containsKey(uplink.toUpperCase());
                })
                .map(d -> index.get(d.getMacAddress().toUpperCase()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(NetworkNode::getMacAddress, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return roots;
    }

    private Map<String, NetworkNode> indexNodes() {
        var devices = repo.findAll();
        var nodes = devices.stream()
                .map(d -> new NetworkNode(d.getMacAddress(), d.getDeviceType()))
                .collect(Collectors.toMap(NetworkNode::getMacAddress, Function.identity()));

        var childrenByUplink = devices.stream()
                .filter(d -> d.getUplinkMacAddress() != null && !d.getUplinkMacAddress().isBlank())
                .collect(Collectors.groupingBy(d -> d.getUplinkMacAddress().toUpperCase()));

        for (var d : devices) {
            var kids = childrenByUplink.getOrDefault(d.getMacAddress().toUpperCase(), List.of());
            var parent = nodes.get(d.getMacAddress());
            for (var kid : kids) parent.getChildren().add(nodes.get(kid.getMacAddress()));
            parent.getChildren().sort(Comparator
                    .comparing((NetworkNode n) -> n.getDeviceType().sortOrder())
                    .thenComparing(NetworkNode::getMacAddress, String.CASE_INSENSITIVE_ORDER));
        }
        return nodes;
    }
}
