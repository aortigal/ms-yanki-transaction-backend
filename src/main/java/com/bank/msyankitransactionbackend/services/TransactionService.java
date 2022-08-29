package com.bank.msyankitransactionbackend.services;

import com.bank.msyankitransactionbackend.handler.ResponseHandler;
import com.bank.msyankitransactionbackend.models.documents.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<ResponseHandler> findAll();

    Mono<ResponseHandler> find(String id);

    Mono<ResponseHandler> create(Transaction transaction);

    Mono<ResponseHandler> update(String id, Transaction transaction);

    Mono<ResponseHandler> delete(String id);
}
