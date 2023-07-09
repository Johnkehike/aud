package com.Auditionapp.Audition.Repository;

import com.Auditionapp.Audition.Entity.Applicants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicants, Long> {

//    @Query(value = "SELECT COUNT(DISTINCT event_name) FROM applicants", nativeQuery = true)
//    int countEvents();

    @Query("SELECT COUNT(DISTINCT a.eventName) FROM Applicants a")
    int countDistinctEvents();

}
