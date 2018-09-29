import java.net.*;
import java.io.*;

class ChatClient {
    private int port = 40202;
    String multicast_address = "239.0.202.1";

    /**
     * Entry of the program.
     * Start the client.
     * @param args
     */
    public static void main(String[] args) {
            try {
                System.setProperty("java.net.preferIPV4Stack", "true");
                ChatClient chatClient = new ChatClient();

                System.out.println("Starting chat client...");

                chatClient.startClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Start the client on desired port and address.
     *
     * @throws Exception
     */
    public void startClient() throws Exception {
        InetAddress group = InetAddress.getByName(multicast_address);
        MulticastSocket multicastSocket = new MulticastSocket(port);
        System.out.println("Started client on address " + multicast_address + ":" + port + "...");
        multicastSocket.joinGroup(group);
        System.out.println("Joined multicast group...");


            ReadThread readThread = new ReadThread(group, port);
            readThread.start();
            System.out.println("Read thread spawned!");
        while (true) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Your message: ");
            String inputMessage = bufferedReader.readLine();
            byte[] message = inputMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
            multicastSocket.send(packet);
            System.out.println("Message sent.");
        }

//        multicastSocket.close();
    }
}

/**
 * Spawn read thread.
 */
class ReadThread extends Thread {
    InetAddress group;
    int port;

    ReadThread(InetAddress group, int port) {
        this.group = group;
        this.port = port;
    }

    public void run() {
        try {
            MulticastSocket read = new MulticastSocket(port);
            read.joinGroup(group);

            byte[] buffer = new byte[100];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length, group, port);
            read.receive(receivedPacket);

            String receivedMessage = new String(receivedPacket.getData());
            System.out.println(receivedPacket.getAddress().toString() + " said: " + receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}