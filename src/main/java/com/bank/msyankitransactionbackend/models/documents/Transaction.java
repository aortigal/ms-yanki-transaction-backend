package com.bank.msyankitransactionbackend.models.documents;

import com.bank.msyankitransactionbackend.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "transactions")
public class Transaction extends Audit {

    @Id
    private String id;

    @NotNull(message = "senderName must not be null")
    private String senderName;

    @NotNull(message = "senderPhonesdasa must not be null")
    private String senderPhone;

    @NotNull(message = "amount must not be null")
    private float amount;

}
