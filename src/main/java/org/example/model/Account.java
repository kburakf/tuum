package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String id;
    private String customerId;
    private String country;
    private Date createTimestamp;
    private Date updateTimestamp;
}