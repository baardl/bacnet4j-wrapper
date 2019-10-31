package no.entra.bacnetk;

import org.code_house.bacnet4j.wrapper.api.*;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.slf4j.Logger;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class BacNetAgent {
    private static final Logger log = getLogger(BacNetAgent.class);
    public static void main(String[] args) {
        int clientDeviceId = 2001;
        BacNetClient client = new BacNetIpClient("192.168.1.31", "255.255.255.255", clientDeviceId);
        client.start();
        Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
        log.info("Found devices: " + devices.size());
        for (Device device : devices) {
            log.info("Device: {}", device);

            for (Property property : client.getDeviceProperties(device)) {
                BacNetToJavaConverter<String> converter = new StringBacNetToJavaConverter();
                try {
                    log.info(property.getName() + " " + client.getPropertyValue(property, converter));
                } catch (BacNetUnknownPropertyException e) {
                    log.info("Property could not be read. Name: {}, Property: {}. ", property.getName(), property);
                }
            }
        }

        client.stop();
    }

    private static class StringBacNetToJavaConverter implements BacNetToJavaConverter<String> {
        @Override
        public String fromBacNet(com.serotonin.bacnet4j.type.Encodable encodable) {
            return encodable.toString();
        }
    }
}
