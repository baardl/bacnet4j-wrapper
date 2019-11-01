package no.entra.bacnet;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import org.code_house.bacnet4j.wrapper.api.BacNetToJavaConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class SerializationHelper {
    private static final Logger log = getLogger(BacNetAgent.class);

    public static void serialize(Object devices, String filename) {

        // Serialization
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(devices);

            out.close();
            file.close();

            log.info("Object has been serialized");

        } catch (IOException ex) {
            log.error("Failed to serialize {}.", devices, ex);
        }
    }

    public static void serializeWithByteQueue(Encodable encodable, String filename ) {
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            ByteQueue byteQueue = new ByteQueue();
            encodable.write(byteQueue);
            byteQueue.write(out);
            out.close();
            file.close();

            log.info("Object has been serialized");

        } catch (IOException ex) {
            log.error("Failed to serialize {}.", encodable, ex);
        }

    }

    public static void main(String[] args) {
        String filename = "devices.ser";
        Object object = deserialize(filename);
        Set<Device> devices = (Set<Device>) object;
        for (Device device : devices) {
            log.info("Device: {}", device);

            Object deserializedProperties = deserialize("device-" + device.getInstanceNumber() + ".ser");
            List<Property> deviceProperties = (List<Property>) deserializedProperties;
            if (deviceProperties != null) {
                log.info("Device name: {} has {} properties.", device.getName(), deviceProperties.size());
                for (Property property : deviceProperties) {
                    log.info("Device: {}, Property {}. Looking for value.", device.getName(), property);
                    BacNetToJavaConverter<String> converter = new BacNetAgent.StringBacNetToJavaConverter();
                    /*
                    try {
                        log.info("Device name: {}; Property name: {}; value {} ", device.getName(), property.getName(), client.getPropertyValue(property, converter));
                    } catch (BacNetUnknownPropertyException e) {
                        log.info("Property could not be read. Device name: {} Property name: {}, Property: {}. ", device.getName(), property.getName(), property);
                    }
                    */
                }
            } else {
                log.debug("No device properties found for device {}", device);
            }
        }
    }

    private static Object deserialize(String filename) {
        Object object = null;
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            object = in.readObject();

            in.close();
            file.close();

            log.info("Object has been deserialized {}", object);
        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
        return object;
    }
}
