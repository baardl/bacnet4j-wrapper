package no.entra.bacnet;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.serotonin.bacnet4j.type.primitive.Time;

import java.io.IOException;

public class TimeAdapter extends TypeAdapter<Time> {
    @Override
    public void write(JsonWriter writer, Time time) throws IOException {
        writer.beginObject();
        writer.name("hour");
        writer.value(time.getHour());
        writer.name("minute");
        writer.value(time.getMinute());
        writer.name("second");
        writer.value(time.getSecond());
        writer.name("hunre");
        writer.value(time.getHundredth());
        writer.endObject();
    }

    @Override
    public Time read(JsonReader jsonReader) throws IOException {
        return new Time(1,2,3,4);
    }
}
