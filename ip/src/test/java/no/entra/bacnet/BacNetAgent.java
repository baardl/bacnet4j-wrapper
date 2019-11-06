package no.entra.bacnet;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import org.code_house.bacnet4j.wrapper.api.BacNetToJavaConverter;
import org.code_house.bacnet4j.wrapper.api.BacNetUnknownPropertyException;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static no.entra.bacnet.SerializationHelper.serialize;
import static no.entra.bacnet.SerializationHelper.serializeWithByteQueue;
import static org.slf4j.LoggerFactory.getLogger;

public class BacNetAgent {
    private static final Logger log = getLogger(BacNetAgent.class);

    public static void main(String[] args) {
        int clientDeviceId = 2001;
        //BacNetClient client = new BacNetIpClient("10.63.23.177", "10.63.23.76", clientDeviceId);
        boolean withRecordingProxy = true;
        IpNetwork ipNetwork = null;
        String ip = "192.168.1.31";
        ip = "10.62.1.11";
        String broadcast = "192.168.1.255";
        broadcast = "10.62.1.11";
        broadcast = "192.168.241.241";
        int port = 47808;
        if (withRecordingProxy) {
            ipNetwork = new IpNetworkBuilder().withLocalBindAddress(ip).withBroadcast(broadcast, 24).withPort(port).buildRecordingProxy();
        } else {
            ipNetwork = new IpNetworkBuilder().withLocalBindAddress(ip).withBroadcast(broadcast, 24).withPort(port).build();
        }
        BacNetIpClient client = new BacNetIpClient(ipNetwork, clientDeviceId);
        client.start();
        log.info("Discovering devices.");
        Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
        log.info("Found devices: " + devices.size());
        serialize(devices, "devices.ser");
        Random random = new Random();
        for (Device device : devices) {
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                log.trace("Interupded");
            }
            log.info("Device: {}", device);
            try {
                List<Property> deviceProperties = client.getDeviceProperties(device);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.trace("Interupded");
                }
                serialize(deviceProperties, "device-" + device.getInstanceNumber() + ".ser");

                if (deviceProperties != null) {
                    log.info("Device name: {} has {} properties.", device.getName(), deviceProperties.size());
                    for (Property property : deviceProperties) {
                        log.info("Device: {}, Property {}. Looking for value.", device.getName(), property);
                        BacNetToJavaConverter<String> converter = new StringBacNetToJavaConverter();
                        try {
                            ReadPropertyAck presentValue = client.getRawPropertyValue(property);
                            String propertyValue = client.convertPropertyValue(presentValue, converter);
                            log.info("Device name: {}; Property name: {}; value {} ", device.getName(), property.getName(), propertyValue);
                            serializeWithByteQueue(presentValue, "device-" + device.getInstanceNumber() + "-property-" + property.getName());
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

    public static class StringBacNetToJavaConverter implements BacNetToJavaConverter<String> {
        @Override
        public String fromBacNet(com.serotonin.bacnet4j.type.Encodable encodable) {
            return encodable.toString();
        }
    }
}
