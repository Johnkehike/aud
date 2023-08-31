package com.Auditionapp.Audition.Repository;

import com.Auditionapp.Audition.Entity.Roles;
import com.Auditionapp.Audition.Entity.Users;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    List<Users> findAll(Sort sort);


    @Query(value = "SELECT * FROM users WHERE role = :role AND created_by = :user", nativeQuery = true)
    List<Users> findAllRoles(@Param("role") String role, @Param("user") String createdBy);

    @Query(value = "SELECT COUNT(*) FROM users WHERE role = :role", nativeQuery = true)
    int countRoles(@Param("role") String role);

    @Query(value = "SELECT COUNT(*) FROM users WHERE role = :role AND created_by = :created", nativeQuery = true)
    int countRoles2(@Param("role") String role, @Param("created") String created_by);


    Users findByEmail(String email);

    Users findByName(String name);

    Users findByUserId(Long id);

    Users findByFullName(String fullName);

    void deleteByUserId(Long id);
}
