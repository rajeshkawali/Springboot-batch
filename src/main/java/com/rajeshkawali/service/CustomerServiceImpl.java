package com.rajeshkawali.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.rajeshkawali.constant.CustomerConstants;
import com.rajeshkawali.dto.CustomerCsv;
import com.rajeshkawali.dto.CustomerDTO;
import com.rajeshkawali.entity.Customer;
import com.rajeshkawali.repository.CustomerRepository;
import com.rajeshkawali.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Rajesh_Kawali
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    public static final String CLASS_NAME = CustomerServiceImpl.class.getName();

    private final CustomerRepository customerRepository;

    @Override
    public Integer uploadCustomers(MultipartFile file) throws Exception {
        String _function = ".uploadCustomers";
        log.info(CLASS_NAME + _function + "::ENTER");
        Set<Customer> customers = csvParser(file);
        customerRepository.saveAll(customers);
        log.info(CLASS_NAME + _function + "::EXIT");
        return customers.size();
    }

    private Set<Customer> csvParser(MultipartFile file) throws Exception {
        String _function = ".csvParser";
        log.info(CLASS_NAME + _function + "::ENTER");
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<CustomerCsv> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CustomerCsv.class);
            CsvToBean<CustomerCsv> csvToBean =
                    new CsvToBeanBuilder<CustomerCsv>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            log.info(CLASS_NAME + _function + "::EXIT");
            return csvToBean.parse()
                    .stream()
                    .map(csvLine -> Customer.builder()
                            .firstName(csvLine.getFirstName())
                            .surname(csvLine.getSurname())
                            .smoothiePreference(csvLine.getSmoothiePreference())
                            .mobileNumber(csvLine.getMobileNumber())
                            .build()
                    )
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        String _function = ".getAllCustomers";
        log.info(CLASS_NAME + _function + "::ENTER");
        List<CustomerDTO> customerList = new ArrayList<>();
        try {
            customerRepository.findAll().forEach(customer -> customerList.add(Util.entityToDto(customer)));
            log.debug(CLASS_NAME + _function + "::Response size is: {}", customerList.size());
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return customerList;
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO customerDto) {
        String _function = ".addCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        Customer customer = null;
        try {
            customer = customerRepository.save(Util.dtoToEntity(customerDto));
            log.debug(CLASS_NAME + _function + "::Customer SuccessFully added to the DB : {}", customer);
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return Util.entityToDto(customer);
    }

    @Override
    public CustomerDTO customerById(Long id) {
        String _function = ".customerById";
        log.info(CLASS_NAME + _function + "::ENTER");
        CustomerDTO customerDTO = null;
        try {
            Optional<Customer> customerOptional = customerRepository.findById(id);
            if (customerOptional.isPresent()) {
                customerDTO = Util.entityToDto(customerOptional.get());
                log.debug(CLASS_NAME + _function + "::Response is: {}", customerDTO);
            }
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return customerDTO;
    }

    @Override
    public CustomerDTO findCustomerBySurname(String surname) {
        String _function = ".findCustomerBySurname";
        log.info(CLASS_NAME + _function + "::ENTER");
        CustomerDTO customerDTO = null;
        try {
            Optional<Customer> customerOptional = customerRepository.findBySurname(surname);
            if (customerOptional.isPresent()) {
                customerDTO = Util.entityToDto(customerOptional.get());
                log.debug(CLASS_NAME + _function + "::Response is: {}", customerDTO);
            }
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return customerDTO;
    }

    @Override
    public String deleteCustomer(Long id) {
        String _function = ".deleteCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        String result = null;
        try {
            CustomerDTO customerDetails = customerById(id);
            if (customerDetails != null) {
                customerRepository.deleteById(id);
                result = CustomerConstants.DELETE_MESSAGE;
                log.debug(CLASS_NAME + _function + "::Deleted status response is: {}", result);
            }
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return result;
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerToUpdate) {
        String _function = ".updateCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        CustomerDTO updatedCustomer = null;
        try {
            CustomerDTO customerDetails = customerById(id);
            if (customerDetails != null) {
                Util.createCustomerEntity(customerToUpdate, customerDetails);
                updatedCustomer = addCustomer(customerDetails);
                log.debug(CLASS_NAME + _function + "::Response is: {}", updatedCustomer);
            }
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception occurred:" + e.getMessage());
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return updatedCustomer;
    }

}