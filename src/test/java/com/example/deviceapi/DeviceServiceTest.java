package com.example.deviceapi;

import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.DeviceType;
import com.example.deviceapi.model.NetworkNode;
import com.example.deviceapi.repo.DeviceRepository;
import com.example.deviceapi.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeviceServiceTest {

    private DeviceService service;
    private DeviceRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DeviceRepository();
        service = new DeviceService(repo);
    }

    @Test
    void testRegisterAndFindDevice() {
        Device d = new Device(DeviceType.GATEWAY, "AA:AA:AA:AA:AA:01", "");
        service.register(d);

        Device found = service.getByMac("AA:AA:AA:AA:AA:01");
        assertThat(found.getDeviceType()).isEqualTo(DeviceType.GATEWAY);
    }

    @Test
    void testRegisterSelfUplinkThrows() {
        Device d = new Device(DeviceType.SWITCH, "AA:AA:AA:AA:AA:02", "AA:AA:AA:AA:AA:02");
        assertThrows(IllegalArgumentException.class, () -> service.register(d));
    }

    @Test
    void testSortOrder() {
        service.register(new Device(DeviceType.ACCESS_POINT, "AA:AA:AA:AA:AA:03", ""));
        service.register(new Device(DeviceType.GATEWAY, "AA:AA:AA:AA:AA:01", ""));
        service.register(new Device(DeviceType.SWITCH, "AA:AA:AA:AA:AA:02", ""));

        List<Device> sorted = service.allSortedByType();

        assertThat(sorted)
                .extracting(Device::getDeviceType)
                .containsExactly(DeviceType.GATEWAY, DeviceType.SWITCH, DeviceType.ACCESS_POINT);
    }

    @Test
    void testTopologyBuildsCorrectly() {
        service.register(new Device(DeviceType.GATEWAY, "AA:AA:AA:AA:AA:01", ""));
        service.register(new Device(DeviceType.SWITCH, "AA:AA:AA:AA:AA:02", "AA:AA:AA:AA:AA:01"));
        service.register(new Device(DeviceType.ACCESS_POINT, "AA:AA:AA:AA:AA:03", "AA:AA:AA:AA:AA:02"));

        List<NetworkNode> roots = service.fullNetworkTopology();
        assertThat(roots).hasSize(1);
        NetworkNode gateway = roots.get(0);
        assertThat(gateway.getChildren()).hasSize(1);
        assertThat(gateway.getChildren().get(0).getDeviceType()).isEqualTo(DeviceType.SWITCH);
        assertThat(gateway.getChildren().get(0).getChildren()).hasSize(1);
    }

    @Test
    void testMultipleRoots() {
        // Two separate networks: Gateway A->Switch A, Gateway B->AccessPoint B
        service.register(new Device(DeviceType.GATEWAY, "AA:AA:AA:AA:AA:01", ""));
        service.register(new Device(DeviceType.SWITCH, "AA:AA:AA:AA:AA:02", "AA:AA:AA:AA:AA:01"));

        service.register(new Device(DeviceType.GATEWAY, "BB:BB:BB:BB:BB:01", ""));
        service.register(new Device(DeviceType.ACCESS_POINT, "BB:BB:BB:BB:BB:02", "BB:BB:BB:BB:BB:01"));

        List<NetworkNode> roots = service.fullNetworkTopology();

        // should detect 2 roots (both gateways)
        assertThat(roots).hasSize(2);

        // verify each gateway has exactly 1 child
        for (NetworkNode root : roots) {
            assertThat(root.getDeviceType()).isEqualTo(DeviceType.GATEWAY);
            assertThat(root.getChildren()).hasSize(1);
        }

        // verify correct sorting: by MAC address ascending
        assertThat(roots.get(0).getMacAddress()).isLessThan(roots.get(1).getMacAddress());
    }

}
