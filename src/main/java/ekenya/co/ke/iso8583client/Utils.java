package ekenya.co.ke.iso8583client;

import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Utils {

    private static ASCIIChannel asciiChannel;
    private static ASCIIChannel asciiChannelNetwork;
    public static ASCIIChannel getChannel() {

        return asciiChannel;
    }

    private static HashMap<String, Integer> requests = new HashMap<>();
    public static HashMap<String, ISOMsg> incomingResponse = new HashMap<>();

    private static AtomicBoolean activeThread = new AtomicBoolean(false);

    public static ASCIIChannel getAsciiChannelNetwork() {

        return asciiChannelNetwork;
    }

    public static boolean createChannel() throws ISOException, IOException {
        GenericPackager packager = new GenericPackager("./packager/genericpackager.xml");

        asciiChannel = new ASCIIChannel("localhost", 8000, packager);
        asciiChannelNetwork = new ASCIIChannel("localhost", 8000, packager);

        // connect
        asciiChannel.connect(); // make a connection to the server
        asciiChannelNetwork.connect(); // make a connection to the server

        return asciiChannel.isConnected() && asciiChannelNetwork.isConnected();
    }

    public static int generateSixDigitNumber() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min + 1) + min;
    }

    public static String generateField37() {
        // Characters to choose from
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder result = new StringBuilder(8);
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            // Append a random character from the characters string
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString().toUpperCase();
    }

    public static void processOutgoingTransaction(ISOMsg isoMsg)
            throws ISOException, IOException {

        String requestReference = isoMsg.getString(11) + isoMsg.getString(37);


        requests.put(requestReference, 1); // action 1 - creating a record in the hashmap

        log.info("sending request with reference -- {}", requestReference);
        asciiChannel.send(isoMsg);


        if (!activeThread.get()) { // if no active thread, create a new thread
            CompletableFuture.runAsync(() -> {
                try {
                    activeThread.set(true);
                    processIncomingTransactionFromServer();
                } catch (ISOException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    public static void processIncomingTransactionFromServer() throws ISOException, IOException {

        log.info("thread created to listen for incoming iso messages--");

        while (!requests.isEmpty()) { // active whenever the hash map has data

            System.out.println("checking for incoming message...");
            ISOMsg isoMsg = asciiChannel.receive();

            String reference  = isoMsg.getString(11) + isoMsg.getString(37);

            log.info("incoming iso message with reference -- {}", reference);

            incomingResponse.put(reference, isoMsg);

            requests.remove(reference); // reduce the hashmap size

        }

        activeThread.set(false);
    }
}
