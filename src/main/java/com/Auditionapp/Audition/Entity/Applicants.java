package com.Auditionapp.Audition.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "applicants")
public class Applicants {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long applicantId;
    @Column(name = "applicant_name")
    private String applicantName;
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "applicant_role")
    private String applicantRole;

    @Column(name = "applicant_score")
    private int applicantScore;

    @Column(name = "producer_name")
    private String producerName;

    private String email;

    private String phone;
}
