package org.example.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private String errorMessage;
    private boolean isSuccess;
}
