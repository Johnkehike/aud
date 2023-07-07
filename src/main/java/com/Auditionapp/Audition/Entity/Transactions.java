package com.Auditionapp.Audition.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long txnId;
    private String name;
    private String email;

    @Transient
    private String[] txnDate;

    private String monthPaid;
    private double amount;
    private String txnRef;

    private String year;

}
