import java.net.*;
import java.io.*;

class ChatClient {
    private int port = 40202;

    public static void main(String[] args) {
        while(true) {
            try {
                System.setProperty("java.net.preferIPV4Stack", "true");
                ChatClient chatClient = new ChatClient();
                chatClient.startClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startClient() throws Exception {
        InetAddress group = InetAddress.getByName("224.0.0.1");
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(group);

        ReadThread readThread = new ReadThread(group, port);
        readThread.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String inputMessage = bufferedReader.readLine();
        byte[] message = inputMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
        multicastSocket.send(packet);
        multicastSocket.close();
    }
}

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
            System.out.println(receivedPacket.getAddress().toString() + "said: " + receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}