package com.sprreact.demo.util;

import com.sprreact.demo.model.Customer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@Scope("singleton")
public class SortingUtil {

    public void sortCustomersByDueTime(List<Customer> customers) {
        Objects.requireNonNull(customers).sort(Comparator.comparing(Customer::getDueTime));
    }
}
