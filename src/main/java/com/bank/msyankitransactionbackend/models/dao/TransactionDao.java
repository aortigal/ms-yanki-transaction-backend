package com.bank.msyankitransactionbackend.models.dao;

import com.bank.msyankitransactionbackend.models.documents.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionDao extends ReactiveMongoRepository<Transaction, String> {
}
