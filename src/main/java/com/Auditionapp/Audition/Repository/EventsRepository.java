package com.Auditionapp.Audition.Repository;

import com.Auditionapp.Audition.Entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {

    List<Events> findByDirectorUserId(String director);

    Events findByEventName(String eventName);

    void deleteByEventId(Long id);

    @Query(value = "SELECT * FROM production_event WHERE producers LIKE CONCAT('%', :producer, '%') AND status = 'AUDITION'", nativeQuery = true)
    List<Events> findEventsByProducer(@Param("producer") String producer);

    @Query(value = "SELECT * FROM production_event WHERE producers LIKE CONCAT('%', :producer, '%') AND status = :status", nativeQuery = true)
    List<Events> findEventsByProducerAndStatus(@Param("producer") String producer, @Param("status") String status);


    @Query(value = "SELECT * FROM production_event WHERE status = :status", nativeQuery = true)
    List<Events> findEventsListByStatus(@Param("status") String status);


//    @Query("SELECT e FROM Events e JOIN e.Applicants a WHERE a.applicantName = :name")
//    List<Events> findEventsByApplicants(@Param("name") String name);

    @Query("SELECT e FROM Events e JOIN Applicants a ON e.eventName = a.eventName WHERE a.applicantName = :name")
    List<Events> findEventsByApplicants(@Param("name") String name);


}
