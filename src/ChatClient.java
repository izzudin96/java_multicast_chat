import java.net.*;
import java.io.*;

class ChatClient {
    private static int port = 32770;
    private static String multicast_address = "239.0.202.1";

    /**
     * Entry of the program.
     * Start the client.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("System client on " + multicast_address + ":" + port);
            try {
                System.setProperty("java.net.preferIPV4Stack", "true");
                ChatClient chatClient = new ChatClient();
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
        multicastSocket.joinGroup(group);
        ReadThread readThread = new ReadThread(group, port, multicastSocket);
        readThread.start();
	while(true) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        	String inputMessage = bufferedReader.readLine();
	        byte[] message = inputMessage.getBytes();

        	DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
        	multicastSocket.send(packet);
	}

    }
}

/**
 * Spawn read thread.
 */
class ReadThread extends Thread {
    InetAddress group;
    int port;
	MulticastSocket multicastSocket;

    ReadThread(InetAddress group, int port, MulticastSocket multicastSocket) {
        this.group = group;
        this.port = port;
	this.multicastSocket = multicastSocket;
    }

    public void run() {
        try {
	while(true) {
            MulticastSocket read = new MulticastSocket(port);
            read.joinGroup(group);

            byte[] buffer = new byte[100];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length, group, port);
            read.receive(receivedPacket);

            String receivedMessage = new String(receivedPacket.getData());
            System.out.println(receivedPacket.getAddress().toString() + ": " + receivedMessage);
	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
