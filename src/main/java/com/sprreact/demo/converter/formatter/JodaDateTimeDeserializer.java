package com.sprreact.demo.converter.formatter;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

public class JodaDateTimeDeserializer extends StdScalarDeserializer<DateTime> {

    public JodaDateTimeDeserializer() {
        super(DateTime.class);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return DateTime.parse(jsonParser.getText().trim());
    }
}
