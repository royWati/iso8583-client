package ekenya.co.ke.iso8583client;

import ekenya.co.ke.iso8583client.model.BalanceDto;
import ekenya.co.ke.iso8583client.model.BalanceResponse;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TransactionService {

    public ResponseEntity<BalanceResponse> getBalance(BalanceDto balanceDto) throws ISOException, IOException {


        ISOMsg isoMsg = new ISOMsg();

        isoMsg.setMTI("0200");
        isoMsg.set(2, balanceDto.getPhoneNumber());
        isoMsg.set(3, "150000");
        isoMsg.set(7, ISODate.getDateTime(new Date())); // date time
        isoMsg.set(11, String.valueOf(Utils.generateSixDigitNumber())); // stan for the transaction
        isoMsg.set(37, Utils.generateField37());


        Utils.processOutgoingTransaction(isoMsg);

        String reference = isoMsg.getString(11) + isoMsg.getString(37);

        int found = 0;

        ISOMsg response  = null;

        LocalDateTime initialTime = LocalDateTime.now();
        while (found == 0) {

            if (Utils.incomingResponse.containsKey(reference)) {

                response = Utils.incomingResponse.get(reference);

                Utils.incomingResponse.remove(reference); //remove the already processed messages

                found = 1;
            }

            if (Duration.between(initialTime, LocalDateTime.now()).getSeconds() > 30) {
                throw new RuntimeException(); // create a timeout exception to handle the response better
            }

        }

        if (response == null) throw new RuntimeException();

        BalanceResponse response1 = BalanceResponse.builder()
                .response(response.getString(39))
                .responseMessage(response.getString(54))
                .balance(response.getString(48))
                .build();

        return ResponseEntity.ok(response1);

    }
}
