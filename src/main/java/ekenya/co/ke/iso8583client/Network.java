package ekenya.co.ke.iso8583client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Network {

    @Autowired
    TaskScheduler taskScheduler;

    ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() throws ISOException, IOException{

        if (!Utils.createChannel()) {
            throw new RuntimeException();
        }

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0800");
        isoMsg.set(7, ISODate.getDateTime(new Date()));
        isoMsg.set(11, String.valueOf(Utils.generateSixDigitNumber()));
        isoMsg.set(48, "10000000");

        System.out.println(isoMsg);

        Utils.getChannel().send(isoMsg);

        ISOMsg response = Utils.getChannel().receive();

        System.out.println("Received response with MTI: " + response.getMTI());
        if (!response.hasField(39)) {
            throw new RuntimeException();
        }

        String responseCode =  response.getString(39).trim();


        if (responseCode.equalsIgnoreCase("000")) {

            // successful sign on, proceed and send echo messages

            System.out.println("executing runnable");
            Runnable runnable = () -> {
                try {
                    runEcho();
                } catch (ISOException | IOException e) {
                    throw new RuntimeException(e);
                }
            };

           executorService.scheduleAtFixedRate(runnable, 5, 10, TimeUnit.SECONDS);

            System.out.println("runnable executed");
        }

    }

    public void runEcho() throws ISOException, IOException {

        System.out.println("running echo");

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0800");
        isoMsg.set(7, ISODate.getDateTime(new Date()));
        isoMsg.set(11, String.valueOf(Utils.generateSixDigitNumber()));
        isoMsg.set(48, "30000000");

        System.out.println(isoMsg);

        log.info("sending echo to the server {}", isoMsg.getString(11));

        Utils.getChannel().send(isoMsg);

        ISOMsg response = Utils.getAsciiChannelNetwork().receive();

        System.out.println("Received response with MTI: " + response.getMTI());
        if (response.hasField(39)) {
            log.info("echo STAn {} | Response Code (Field 39): {}",response.getString(11)
                    ,response.getString(39));
        }
    }


}
