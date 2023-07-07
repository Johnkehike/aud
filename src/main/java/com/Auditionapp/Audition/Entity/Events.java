package com.Auditionapp.Audition.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "production_event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name")
    private String eventName;

    private String location;

    @Column(name = "dir_user_id")
    private String directorUserId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Transient
    private String statusTest;

    private String producers;

    @Transient
    private List<String> producerList;

    private String startDate;

    private String roles;
}
