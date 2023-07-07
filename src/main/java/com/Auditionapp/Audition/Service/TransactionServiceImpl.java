package com.Auditionapp.Audition.Service;

import com.Auditionapp.Audition.Entity.Transactions;
import com.Auditionapp.Audition.Repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionsRepository transactionsRepository;
    @Override
    public void saveTransactions(Transactions transactions) {
        log.info("Saving transactions");
        transactionsRepository.save(transactions);
    }
}
