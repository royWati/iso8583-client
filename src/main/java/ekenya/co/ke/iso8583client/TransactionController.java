package ekenya.co.ke.iso8583client;

import ekenya.co.ke.iso8583client.model.BalanceDto;
import lombok.RequiredArgsConstructor;
import org.jpos.iso.ISOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/balance")
    public ResponseEntity<?> getCustomerBalance(@RequestBody BalanceDto balanceDto)
            throws ISOException, IOException {
        return transactionService.getBalance(balanceDto);
    }

    @PostMapping("/funds-transfer")
    public ResponseEntity<?> fundsTransfer() {
        return null;
    }

    @PostMapping("/reverse-transaction")
    public ResponseEntity<?> reverseTransaction() {
        return null;
    }

}
