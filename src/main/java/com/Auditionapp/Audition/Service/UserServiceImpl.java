package com.Auditionapp.Audition.Service;

import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;
    @Override
    public void addNewUser(Users users) {

        usersRepository.save(users);

    }
}
