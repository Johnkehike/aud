package com.Auditionapp.Audition.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.threeten.bp.LocalDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long visitorId;

    private String visitor_name;
    private String visitor_email;
    private String visitor_phone;

    private String dateOfVisit;
    private int visitor_duration;
    private String location;
    private String visitor_code;
    private String whomToSee;
    private String residentEmail;
    private String entry_status;

    private String expectedDepartureDate;

}
