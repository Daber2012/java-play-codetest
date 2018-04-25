package com.sprreact.demo.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprreact.demo.model.Customer;
import com.sprreact.demo.util.SortingUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileToJSONConverter {

    private final String DEFAULT_DATE_TIME_ZONE = "Europe/London";

    private final @NonNull SortingUtil sortingUtil;

    public String fileToCustomersSortedJSONArray(File file) {
        String jsonString = "";
        try {
            jsonString = fileToJSONString(file);
            List<Customer> customers = jsonNodeToListCustomers(stringToJSONNode(jsonString));
            if (nonNull(customers)) {
                convertDatesToOneTimeZone(customers);
                sortingUtil.sortCustomersByDueTime(customers);
                jsonString = customersToJsonString(customers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    private String fileToJSONString (File file) throws IOException {
        String content;
        content = new String(Files.readAllBytes(file.toPath()));
        return content;
    }

    public JsonNode stringToJSONNode(String content) throws IOException {
        return new ObjectMapper().readTree(content);
    }

    public List<Customer> jsonNodeToListCustomers(JsonNode jsonNode) throws IOException {
        if (nonNull(jsonNode) && jsonNode.isArray()) {
            return new ObjectMapper().readerFor(new TypeReference<List<Customer>>(){}).readValue(jsonNode);
        } else {
            return null;
        }
    }

    public String customersToJsonString(List<Customer> customers) throws IOException {
        StringWriter stringWriter = new StringWriter();
        new ObjectMapper().writeValue(stringWriter, customers);
        return stringWriter.toString();
    }

    private void convertDatesToOneTimeZone(List<Customer> customers) {
        customers.forEach(customer -> {
            customer.setDueTime(customer.getDueTime().withZone(DateTimeZone.forID(DEFAULT_DATE_TIME_ZONE)));
            customer.setJoinTime(customer.getJoinTime().withZone(DateTimeZone.forID(DEFAULT_DATE_TIME_ZONE)));
        });
    }
}
