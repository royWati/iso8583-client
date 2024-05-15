package ekenya.co.ke.iso8583client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponse {

    private String response;
    private String responseMessage;
    private String balance;
}
