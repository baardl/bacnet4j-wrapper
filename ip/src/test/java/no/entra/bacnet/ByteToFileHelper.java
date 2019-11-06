package no.entra.bacnet;

import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class ByteToFileHelper {
    private static final Logger log = getLogger(ByteToFileHelper.class);

    private static final String BACNET_HEX = "810a04060104";

    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\gp694\\examples\\bacnet\\bacnet4j-wrapper\\packetdata";
       // writeHexStringToFile(fileName);
        byte[] readBytes = readFirstBytesFromFile(1, fileName);
        String hexAsString = bytesToHex(readBytes);
        log.info("Hex: {} ", hexAsString);
        return;
    }

    public static void writeHexStringToFile(String fileName) throws IOException {
        OutputStream os = new FileOutputStream(fileName);
        String text = "81";
        byte[] value = hexStringToByteArray(BACNET_HEX);
        os.write(value);
        os.close();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public static byte[] readFromFile(String filename) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(filename));
        return content;
    }

    public static byte[] readFirstBytesFromFile(int length, String filename) throws IOException {
        byte[] buffer = new byte[length];
        InputStream is = new FileInputStream(filename);
        if (is.read(buffer) != buffer.length) {
            // do something
        }
        is.close();
        return buffer;
    }
}
