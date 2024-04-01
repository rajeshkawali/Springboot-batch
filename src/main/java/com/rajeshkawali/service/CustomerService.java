package com.rajeshkawali.service;

import com.rajeshkawali.dto.CustomerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Rajesh_Kawali
 */
public interface CustomerService {

    public List<CustomerDTO> getAllCustomers();

    public CustomerDTO addCustomer(CustomerDTO customerDto);

    public CustomerDTO customerById(Long id);

    public String deleteCustomer(Long id);

    public CustomerDTO updateCustomer(Long id, CustomerDTO user);

    public CustomerDTO findCustomerBySurname(String surname);

    public Integer uploadCustomers(MultipartFile file) throws Exception;
}