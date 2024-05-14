package ekenya.co.ke.iso8583client;

import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class Network {

    public static void main(String... args) throws ISOException, IOException {

        GenericPackager packager = new GenericPackager("./packager/genericpackager.xml");

        ASCIIChannel asciiChannel = new ASCIIChannel("localhost", 8000, packager);

        // connect
        asciiChannel.connect(); // make a connection to the server

        // send a sign on request

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0800");
        isoMsg.set(7, ISODate.getDateTime(new Date()));
        isoMsg.set(11, String.valueOf(generateSixDigitNumber()));
        isoMsg.set(48, "10000000");

        System.out.println(isoMsg);

        asciiChannel.send(isoMsg);

        ISOMsg response = asciiChannel.receive();

        System.out.println("Received response with MTI: " + response.getMTI());
        if (response.hasField(39)) {
            System.out.println("Response Code (Field 39): " + response.getString(39));
        }

        // Disconnect after sending the message
        asciiChannel.disconnect();

    }

    public static int generateSixDigitNumber() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min + 1) + min;
    }


}
