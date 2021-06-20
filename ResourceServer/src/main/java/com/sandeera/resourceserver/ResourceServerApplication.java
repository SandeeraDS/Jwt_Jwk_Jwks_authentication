package com.sandeera.resourceserver;

import com.sandeera.resourceserver.Bean.Customer;
import com.sandeera.resourceserver.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class ResourceServerApplication {

    @Autowired
    private CustomerRepository customerRepository;

    @PostConstruct
    public void initUsers() {
        List<Customer> customers = Stream.of(
                new Customer(1001, "firstName0", "lastName0", "customer0@gmail.com","countryA"),
                new Customer(1002, "firstName1", "lastName1", "customer1@gmail.com","countryA"),
                new Customer(1003, "firstName2", "lastName2", "customer2@gmail.com","countryB"),
                new Customer(1004, "firstName3", "lastName3", "customer3@gmail.com","countryA"),
                new Customer(1005, "firstName4", "lastName4", "customer4@gmail.com","countryC")
        ).collect(Collectors.toList());
        customerRepository.saveAll(customers);
    }

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }

}
