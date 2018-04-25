package com.sprreact.demo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sprreact.demo.converter.FileToJSONConverter;
import com.sprreact.demo.converter.formatter.JodaDateTimeDeserializer;
import com.sprreact.demo.converter.formatter.JodaDateTimeSerializer;
import com.sprreact.demo.model.Customer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private FileToJSONConverter fileToJsonConverter;

    private ObjectMapper objectMapper;
    private Customer testCustomer;

    @Before
    public void init() {
        objectMapper = new ObjectMapper();
        testCustomer = new Customer(10000000, "Ulysses Leon",
                DateTime.parse("2014-06-18T06:26:56-07:00"),
                DateTime.parse("2015-04-08T12:47:16-07:00"));
    }

	@Test
    public void testJsonReading() {
	    File file = new File("customers.json");
	    String result = fileToJsonConverter.fileToCustomersSortedJSONArray(file);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testStringToJson() throws IOException {
	    String jsonString = "[\n" +
                "  {\n" +
                "    \"id\": 10000000,\n" +
                "    \"name\": \"Ulysses Leon\",\n" +
                "    \"duetime\": \"2014-06-18T06:26:56-07:00\",\n" +
                "    \"jointime\": \"2015-04-08T12:47:16-07:00\"\n" +
                "  }]";
	    JsonNode jsonNode = fileToJsonConverter.stringToJSONNode(jsonString);
	    assertTrue(jsonNode.isArray());
        assertTrue(jsonNode.get(0).get("name").toString().equals("\"Ulysses Leon\""));
    }

    @Test
    public void testJsonNodeToListCustomers() throws IOException {
	    JsonNode arrayNode = objectMapper.createArrayNode();
	    ObjectNode customerNode = objectMapper.createObjectNode();
	    customerNode.put("id", 10000000);
	    customerNode.put("name", "Ulysses Leon");
	    customerNode.put("duetime", "2014-06-18T06:26:56-07:00");
        customerNode.put("jointime", "2015-04-08T12:47:16-07:00");
        ((ArrayNode) arrayNode).add(customerNode);
        List<Customer> customers = new ArrayList<>();
        customers.add(testCustomer);
	    List<Customer> testList = fileToJsonConverter.jsonNodeToListCustomers(arrayNode);
        assertTrue(customers.get(0).equals(testList.get(0)));
    }

    @Test
    public void testCustomersToJsonString() throws IOException {
        List<Customer> customers = new ArrayList<>();
        customers.add(testCustomer);
        String result = fileToJsonConverter.customersToJsonString(customers);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testDateTimeSerializer() throws IOException {
        Writer writer = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        new JodaDateTimeSerializer().serialize(new DateTime("2015-04-08T12:47:16-07:00"), jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        assertThat(writer.toString(), equalTo("\"2015-04-08T22:47:16+0300\""));
    }

    @Test
    public void testDateTimeDeserializer() throws IOException {
        String date = "\"2014-06-18T06:26:56-07:00\"";
        String json = String.format("{\"datetime\":%s}", date);
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonParser jsonParser = objectMapper.getFactory().createParser(stream);
        DeserializationContext deserializationContext = objectMapper.getDeserializationContext();
        jsonParser.nextToken();
        jsonParser.nextToken();
        jsonParser.nextToken();
        DateTime parsedDateTime = new JodaDateTimeDeserializer().deserialize(jsonParser, deserializationContext);
        DateTime dateTime = DateTime.parse(date.substring(1,26));
        assertTrue(dateTime.equals(parsedDateTime));
    }

}
