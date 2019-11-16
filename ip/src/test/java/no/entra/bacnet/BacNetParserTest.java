package no.entra.bacnet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedCovNotificationRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BacNetParserTest {

    public BacNetParserTest() throws BACnetException {
    }

    @Test
    void parseUnconfirmedCovNotificationServiceDescription() throws BACnetException, JsonProcessingException {
        final byte type = UnconfirmedCovNotificationRequest.TYPE_ID;
        String byteHex = "0209001c020007d12c020007d139004e09702e91002f09cb2e2ea4770b0105b40d2300442f2f09c42e91002f4f";
        final ByteQueue queue = new ByteQueue(byteHex);
        UnconfirmedCovNotificationRequest requestService = (UnconfirmedCovNotificationRequest) UnconfirmedRequestService.createUnconfirmedRequestService(type, queue);
        assertNotNull(requestService);
        assertTrue(requestService instanceof UnconfirmedCovNotificationRequest);
        ObjectMapper mapper = new ObjectMapper();
//        Gson gson = new GsonBuilder()
//                .create();
//        final String json = gson.toJson((UnconfirmedCovNotificationRequest)requestService);
        String json = mapper.writeValueAsString((UnconfirmedCovNotificationRequest) requestService);
        System.out.println(json);
    }

    @Test
    void testMe() throws BACnetException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Encodable.class, new EncodableAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        final byte type = UnconfirmedCovNotificationRequest.TYPE_ID;
        String byteHex = "0209001c020007d12c020007d139004e09702e91002f09cb2e2ea4770b0105b40d2300442f2f09c42e91002f4f";
        final ByteQueue queue = new ByteQueue(byteHex);
        UnconfirmedRequestService requestService = UnconfirmedRequestService.createUnconfirmedRequestService(type, queue);

        String json = gson.toJson(requestService);
        System.out.println(json);
    }
}
