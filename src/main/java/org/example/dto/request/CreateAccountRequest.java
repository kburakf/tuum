package org.example.dto.request;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest implements Serializable {
    private String customerId;
    private String country;
    private List<String> currencies;
}
