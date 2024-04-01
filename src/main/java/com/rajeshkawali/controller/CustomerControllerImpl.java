package com.rajeshkawali.controller;


import com.rajeshkawali.dto.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author Rajesh_Kawali
 *
 */
@Tag(name = "Customer", description = "Customer management APIs")
public interface CustomerControllerImpl {


    @Tag(name = "importCsvToDBJob", description = "import customer details csv to DB job")
    public void importCsvToDBJob();


    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = MultipartFile.class), mediaType = MediaType.MULTIPART_FORM_DATA_VALUE) })
    @Operation(
            summary = "Upload customers to DB",
            description = "This api is used to upload customer csv file and convert to object then store in database",
            tags = {"uploadCustomers"})
    public ResponseEntity<Integer> uploadCustomers(@RequestPart("file") MultipartFile file) throws Exception;


    @Tag(name = "getAllCustomers", description = "Get All Customers APIs")
    @Operation(summary = "Retrieve customers", tags = {"GET"}, description = "Get a customer object by specifying its id. The response is customer object provided back to client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found.", content = {@Content(schema = @Schema()) }) })
    public ResponseEntity<List<CustomerDTO>> getAllCustomers();


    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = CustomerDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE) })
    @Operation(
            summary = "Add a new customer.",
            description = "This api is used to add a new customer in database",
            tags = {"addCustomer"})
    //@Tag(name = "addCustomer", description = "Add customer details")
    public ResponseEntity<?> addCustomer(
            @Parameter(description = "Customer details") @Valid @RequestBody CustomerDTO customerDTO);


    //@Tag(name = "CustomerById", description = "Get customer using customer id")
    @Operation(
            summary = "Retrieve a customer by Id",
            description = "Get a customer object by specifying its id.",
            tags = { "customerById", "GET"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = CustomerDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(description = "No customer found")) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema(description = "Server error")) }) })
    public ResponseEntity<?> customerById(
            @Parameter(description = "Customer id", required = true) @PathVariable Long id);


    //@Tag(name = "findCustomerBySurname", description = "Get customer using surname")
    @Operation(
            summary = "Retrieve a customer by surname",
            description = "Get a customer object by specifying its surname.",
            tags = { "findCustomerBySurname", "GET"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = CustomerDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE) }),
            @ApiResponse(responseCode = "404", description = "No customer found") })
    public ResponseEntity<?> findCustomerBySurname(@Parameter(explode = Explode.TRUE, name = "surname", in = ParameterIn.QUERY, description = "Customer surname", style = ParameterStyle.FORM, schema = @Schema(type = "string", defaultValue = "available", allowableValues = { "koli", "kawali", "joshi" }))
                                                   @RequestParam String surname);

    @Parameters({
            @Parameter(name = "id", description = "customer id to search and update", required = true),
            @Parameter(name = "CustomerDTO", description = "Customer details to update") })
    @ApiResponses({ @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(description = "Customer details are successfully updated to the DB"), mediaType = MediaType.APPLICATION_JSON_VALUE) }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(schema = @Schema(description = "Customer not found for the given id")) }) })
    //@Tag(name = "updateCustomer", description = "Update the customer details")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO);


    @Operation(summary = "Delete customer by Id", description = "This api is used to delete the customer by id", tags = {
            "deleteCustomer"})
    @ApiResponses({ @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = CustomerDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE) }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(schema = @Schema(description = "Customer not found for given Id")) }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(schema = @Schema(defaultValue = "Server error")) }) })
    //@Tag(name = "deleteCustomer", description = "Delete the customer details")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "Customer id to delete", required = true) @PathVariable Long id);

}
