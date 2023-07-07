package com.Auditionapp.Audition.Controller;


import com.Auditionapp.Audition.Entity.Transactions;
import com.Auditionapp.Audition.Service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/txns")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/log")
    public ResponseEntity<String> addNewTransactionRecord(@RequestBody Transactions transactions){

//    public ResponseEntity<String> addNewTransactionRecord(@RequestParam("name") String name,
//                                                          @RequestParam("email") String email,
//                                                          @RequestParam("amount") double amount,
//                                                          @RequestParam("date") String[] billDate,
//                                                          @RequestParam("ref") String ref, Transactions transactions){




        log.info("Inside Transaction Controller class");
        log.info(Arrays.toString(transactions.getTxnDate()));
        String[] checkedMonths = transactions.getTxnDate();

        for(int i=0; i<checkedMonths.length; i++) {


            Transactions txns = new Transactions();
            txns.setMonthPaid(checkedMonths[i]);
            txns.setName(transactions.getName());
            txns.setTxnRef(transactions.getTxnRef());
            txns.setEmail(transactions.getEmail());
            txns.setAmount(transactions.getAmount());
            txns.setYear(transactions.getYear());

            log.info(checkedMonths[i]);
            transactionService.saveTransactions(txns);
        }

        return ResponseEntity.ok("Transaction saved successfully");

    }
}
