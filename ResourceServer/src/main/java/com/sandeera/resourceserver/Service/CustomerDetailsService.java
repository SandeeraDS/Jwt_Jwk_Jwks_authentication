package com.sandeera.resourceserver.Service;

import com.sandeera.resourceserver.Bean.Customer;
import com.sandeera.resourceserver.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerDetailsService {

    @Autowired
    private CustomerRepository customerRepository;


    public List<Customer> getAllCustomerDetails(){
        return customerRepository.findAll();
    }
}
