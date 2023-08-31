package com.Auditionapp.Audition.Repository;

import com.Auditionapp.Audition.Entity.Applicants;
import com.Auditionapp.Audition.Entity.Events;
import com.Auditionapp.Audition.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicants, Long> {

//    @Query(value = "SELECT COUNT(DISTINCT event_name) FROM applicants", nativeQuery = true)
//    int countEvents();

    Applicants findByApplicantId(Long applicantId);

    Applicants findByEmail(String email);

    Applicants findByEmailAndEventName(String email, String event);


    void deleteByApplicantId(Long applicantId);

    @Query("SELECT COUNT(DISTINCT a.eventName) FROM Applicants a")
    int countDistinctEvents();

    @Query("SELECT a FROM Applicants a JOIN Events e ON a.eventName = e.eventName WHERE e.status = :status AND e.directorUserId = :director")
    List<Applicants> findApplicantsForDirectors(@Param("status") Status name, @Param("director") String director);


//    @Query("SELECT a FROM Applicants a JOIN Events e ON a.eventName = a.eventName WHERE e.status = :status AND e.producers LIKE CONCAT('%', :producer, '%')")
//    List<Applicants> findApplicantsForProducers(@Param("status") Status name, @Param("producer") String producer);


    @Query("SELECT DISTINCT a FROM Applicants a JOIN Events e ON a.eventName = e.eventName WHERE e.status = :status AND e.producers LIKE CONCAT('%', :producer, '%')")
    List<Applicants> findApplicantsForProducers(@Param("status") Status status, @Param("producer") String producer);

    @Query(value = "SELECT * FROM applicants WHERE applicant_name = :applicant", nativeQuery = true)
    List<Applicants> findEventsForApplicants(@Param("applicant") String applicant);

}
