package no.entra.bacnet;

import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static com.serotonin.bacnet4j.npdu.ip.IpNetwork.MESSAGE_LENGTH;
import static no.entra.bacnet.ByteToFileHelper.readFirstBytesFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class PacketDataTest {
    private static final Logger log = getLogger(PacketDataTest.class);
   // private static String packetData = "�\u000B \u0007\u0001�                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          �\u000B 4\u0001 \u0010\u0002\t \u001C\u0002 \u0007�,\u0002 \u0007�9 N\tp.� /\t�..�w\u000B\u0004\u0001�\u0014\u0014\u0011\u0006//\t�.� /O                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            �\u000B \f\u0001 �� �\u0010\b \u0007�,\u0002 \u0007�9 N\tp.� /\t�..�w\u000B\u0004\u0001�\u0014\u0014\u0011\u0006//\t�.� /O                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            �\u000B \u0019\u0001 �� �\u0010 �\u0002 \u0007�\"\u0005đ \"\u0003a.� /\t�..�w\u000B\u0004\u0001�\u0014\u0014\u0011\u0006//\t�.� /O                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            �";


    @Test
    public void verifyBacnetContent() throws IOException {
        String filename = "C:\\Users\\gp694\\examples\\bacnet\\bacnet4j-wrapper\\packetdata";
        /*
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        packetData = line.substring(4,MESSAGE_LENGTH + 2);
         */
        final byte[] buffer = readFirstBytesFromFile(MESSAGE_LENGTH, filename); // new byte[MESSAGE_LENGTH];
         DatagramPacket p = new DatagramPacket(buffer, buffer.length);
         //int length = packetData.getBytes().length;
         log.debug("Length {}", buffer.length);

        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        p.setAddress(inetAddress);
        p.setPort(47808);
        int bytesIn = p.getLength();
        final ByteQueue queue = new ByteQueue(buffer, 0, p.getLength());
        final OctetString link = IpNetworkUtils.toOctetString(p.getAddress().getAddress(), p.getPort());
        for (int i = 0; i < 10; i++) {
            byte nextByte = queue.pop();
            log.trace("i: {}; byte: {}", i, String.format("%02x", nextByte));
        }
        assertEquals("81", queue.pop());
        /*
         InetAddress inetAddress = InetAddress.getByName(localBindAddressStr);
                p.setAddress(inetAddress);
                p.setPort(port);
                bytesIn += p.getLength();
                String filename = "packetdata";
                filename = "testdata";
                // byte[] packetData = Files.readAllBytes(Paths.get(filename));
                // Create a new byte queue for the message, because the queue will probably be processed in the
                // transport thread.
                reader = new BufferedReader(new FileReader(filename));
                String line = reader.readLine();
                while (line != null) {
                    byte[] packetData = line.getBytes();
                    final ByteQueue queue = new ByteQueue(packetData, 0, p.getLength());
                    final OctetString link = IpNetworkUtils.toOctetString(p.getAddress().getAddress(), p.getPort());
                    LOG.debug("Reading PacketData from file: {}", filename);
                    handleIncomingData(queue, link);

                    // read next line
                    line = reader.readLine();

                    // Reset the packet.
                    p.setData(buffer);
                }
         */

    }

}
