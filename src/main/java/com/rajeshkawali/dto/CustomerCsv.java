package com.rajeshkawali.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

/**
 * @author Rajesh_Kawali
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCsv {

    @CsvBindByName(column = "id")
    private Long id;

    @CsvBindByName(column = "firstName")
    private String firstName;

    @CsvBindByName(column = "surname")
    private String surname;

    @CsvBindByName(column = "smoothiePreference")
    private String smoothiePreference;

    @CsvBindByName(column = "mobileNumber")
    private Long mobileNumber;
}