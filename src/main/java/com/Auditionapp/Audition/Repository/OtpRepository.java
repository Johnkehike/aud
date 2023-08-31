package com.Auditionapp.Audition.Repository;

import com.Auditionapp.Audition.Entity.OtpModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpModel, Long> {

    OtpModel findByEmailAndStatus(String email, String status);
    OtpModel findByEmail(String email);
}
