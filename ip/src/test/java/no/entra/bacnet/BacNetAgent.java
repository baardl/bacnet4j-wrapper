package no.entra.bacnet;

import org.code_house.bacnet4j.wrapper.api.*;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class BacNetAgent {
    private static final Logger log = getLogger(BacNetAgent.class);
    public static void main(String[] args) {
        int clientDeviceId = 2001;
        BacNetClient client = new BacNetIpClient("10.63.23.177", "10.63.23.255", clientDeviceId);
        client.start();
        Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
        log.info("Found devices: " + devices.size());
        for (Device device : devices) {
            log.info("Device: {}", device);
            try {
                List<Property> deviceProperties = client.getDeviceProperties(device);
                if (deviceProperties != null) {
                    for (Property property : deviceProperties) {
                        BacNetToJavaConverter<String> converter = new StringBacNetToJavaConverter();
                        try {
                            log.info("Device name: {}; Property name: {}; value {} ", device.getName(), property.getName(), client.getPropertyValue(property, converter));
                        } catch (BacNetUnknownPropertyException e) {
                            log.info("Property could not be read. Device name: {} Property name: {}, Property: {}. ", device.getName(), property.getName(), property);
                        }
                    }
                } else {
                    log.debug("No device properties found for device {}", device);
                }
            } catch (Exception e) {
                log.info("Failed to find info for device {}", device, e);
            }
        }
        log.info("Done.");
        client.stop();
    }

    private static class StringBacNetToJavaConverter implements BacNetToJavaConverter<String> {
        @Override
        public String fromBacNet(com.serotonin.bacnet4j.type.Encodable encodable) {
            return encodable.toString();
        }
    }
}
