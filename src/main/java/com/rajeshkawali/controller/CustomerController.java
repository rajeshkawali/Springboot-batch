package com.rajeshkawali.controller;

import com.rajeshkawali.dto.CustomerDTO;
import com.rajeshkawali.exception.ResponseStatus;
import com.rajeshkawali.service.CustomerService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh_Kawali
 */
@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerControllerImpl {

    public static final String CLASS_NAME = CustomerController.class.getName();

    private final CustomerService customerService;

    private final JobLauncher jobLauncher;

    private final Job job;

    @PostMapping("/v1/customer/importCsv")
    public void importCsvToDBJob() {
        String _function = ".importCsvToDBJob";
        log.info(CLASS_NAME + _function + "::ENTER");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("start", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception : {}", e);
        }
        log.info(CLASS_NAME + _function + "::EXIT");
    }

    @PostMapping(value = "/v1/customer/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Integer> uploadCustomers(@RequestPart("file") MultipartFile file) throws Exception {
        String _function = ".uploadCustomers";
        log.info(CLASS_NAME + _function + "::ENTER");
        Integer uploadedCustomerCount = 0;
        try {
            uploadedCustomerCount = customerService.uploadCustomers(file);
            log.info(CLASS_NAME + _function + "::No of customers uploaded is: {}", uploadedCustomerCount);
        } catch (Exception e) {
            log.error(CLASS_NAME + _function + "::Exception : {}", e);
        }
        log.info(CLASS_NAME + _function + "::EXIT");
        return ResponseEntity.ok(uploadedCustomerCount);
    }

    @GetMapping("/v1/customer/getAll")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        String _function = ".getAllCustomers";
        log.info(CLASS_NAME + _function + "::ENTER");
        List<CustomerDTO> customerList = new ArrayList<>();
        customerList = customerService.getAllCustomers();
        log.info(CLASS_NAME + _function + "::EXIT");
        return ResponseEntity.status(HttpStatus.OK).body(customerList);
    }

    @PostMapping("/v1/customer/add")
    public ResponseEntity<?> addCustomer(
            @Parameter(description = "Customer details") @Valid @RequestBody CustomerDTO customerDTO) {
        String _function = ".addCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        log.debug(CLASS_NAME + _function + "::Customer details: {}", customerDTO);
        CustomerDTO addedCustomer = customerService.addCustomer(customerDTO);
        log.info(CLASS_NAME + _function + "::EXIT");
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCustomer);

    }

    @GetMapping("/v1/customer/{id}")
    public ResponseEntity<?> customerById(
            @Parameter(description = "Customer id", required = true) @PathVariable Long id) {
        String _function = ".customerById";
        log.info(CLASS_NAME + _function + "::ENTER");
        log.debug(CLASS_NAME + _function + "::Requested customer id: {} ", id);
        CustomerDTO addedCustomer = customerService.customerById(id);
        if (addedCustomer != null) {
            log.info(CLASS_NAME + _function + "::EXIT");
            return ResponseEntity.status(HttpStatus.OK).body(addedCustomer);
        } else {
            log.error(CLASS_NAME + _function + "::Customer not available for given id: {}", id);
            throw ResponseStatus.idNotFound.apply(id);
        }
    }

    @GetMapping("/v1/customer")
    public ResponseEntity<?> findCustomerBySurname(@Parameter(explode = Explode.TRUE, name = "surname", in = ParameterIn.QUERY, description = "Customer surname", style = ParameterStyle.FORM, schema = @Schema(type = "string", defaultValue = "available", allowableValues = {"koli", "kawali", "joshi"}))
                                                   @RequestParam String surname) {
        String _function = ".findCustomerBySurname";
        log.info(CLASS_NAME + _function + "::ENTER");
        log.debug(CLASS_NAME + _function + "::Customer surname: {} ", surname);
        CustomerDTO addedCustomer = customerService.findCustomerBySurname(surname);
        if (addedCustomer != null) {
            log.info(CLASS_NAME + _function + "::EXIT");
            return ResponseEntity.status(HttpStatus.OK).body(addedCustomer);
        } else {
            log.error(CLASS_NAME + _function + "::Customer not available for given surname: {}", surname);
            throw ResponseStatus.nameNotFound.apply(surname);
        }
    }

    @PutMapping("/v1/customer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        String _function = ".updateCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        log.debug(CLASS_NAME + _function + "::Customer details to update-> id: {}, Customer details: {}", id,
                customerDTO);
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        if (updatedCustomer != null) {
            log.info(CLASS_NAME + _function + "::EXIT");
            return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
        } else {
            log.error(CLASS_NAME + _function + "::Customer not available for given Id: {} ", id);
            throw ResponseStatus.idNotFound.apply(id);
        }
    }

    @DeleteMapping("/v1/customer/{id}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "Customer id to delete", required = true) @PathVariable Long id) {
        String _function = ".deleteCustomer";
        log.info(CLASS_NAME + _function + "::ENTER");
        log.debug(CLASS_NAME + _function + "::Customer id to delete from db: {} ", id);
        String result = customerService.deleteCustomer(id);
        if (result != null) {
            log.info(CLASS_NAME + _function + "::EXIT");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            log.error(CLASS_NAME + _function + "::Customer not available for given id: {} ", id);
            throw ResponseStatus.idNotFound.apply(id);
        }
    }

}