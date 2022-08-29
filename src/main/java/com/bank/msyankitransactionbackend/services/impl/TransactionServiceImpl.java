package com.bank.msyankitransactionbackend.services.impl;

import com.bank.msyankitransactionbackend.constants.Constant;
import com.bank.msyankitransactionbackend.handler.ResponseHandler;
import com.bank.msyankitransactionbackend.models.dao.TransactionDao;
import com.bank.msyankitransactionbackend.models.documents.Transaction;
import com.bank.msyankitransactionbackend.models.utils.DataEvent;
import com.bank.msyankitransactionbackend.producer.KafkaProducer;
import com.bank.msyankitransactionbackend.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao dao;

    @Autowired
    private KafkaProducer kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public Mono<ResponseHandler> findAll() {
        log.info("[INI] findAll Transaction");
        return dao.findAll()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .collectList()
                .map(transactions -> new ResponseHandler(Constant.RESPONSE_DONE, HttpStatus.OK, transactions))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Transaction"));
    }

    @Override
    public Mono<ResponseHandler> find(String id) {
        log.info("[INI] find Transaction");
        return dao.findById(id)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> new ResponseHandler(Constant.RESPONSE_DONE, HttpStatus.OK, transaction))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Transaction"));
    }

    @Override
    public Mono<ResponseHandler> create(Transaction transaction) {
        log.info("[INI] create Transaction");
        DataEvent<Transaction> dataEvent = new DataEvent<>();
        dataEvent.setId(UUID.randomUUID().toString());
        dataEvent.setProcess(Constant.PROCESS_TRANSACTION_CREATE);
        dataEvent.setDateEvent(LocalDateTime.now());
        dataEvent.setData(transaction);

        kafkaProducer.sendMessage(dataEvent);
        log.info("[END] create Transaction");
        return Mono.just(new ResponseHandler(Constant.RESPONSE_DONE, HttpStatus.OK, transaction));
    }

    @Override
    public Mono<ResponseHandler> update(String id, Transaction transaction) {
        log.info("[INI] update Transaction");
        DataEvent<Transaction> dataEvent = new DataEvent<>();
        dataEvent.setId(UUID.randomUUID().toString());
        dataEvent.setProcess(Constant.PROCESS_TRANSACTION_UPDATE);
        dataEvent.setDateEvent(LocalDateTime.now());

        transaction.setId(id);
        dataEvent.setData(transaction);

        kafkaProducer.sendMessage(dataEvent);
        log.info("[END] update Transaction");
        return Mono.just(new ResponseHandler(Constant.RESPONSE_DONE, HttpStatus.OK, transaction));
    }

    @Override
    public Mono<ResponseHandler> delete(String id) {
        log.info("[INI] delete Transaction");

        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(new ResponseHandler(Constant.RESPONSE_DONE, HttpStatus.OK, null)));
            else
                return Mono.just(new ResponseHandler(Constant.RESPONSE_NOT_FOUND, HttpStatus.NOT_FOUND, null));
        }).doFinally(fin -> log.info("[END] delete Transaction"));
    }
}
