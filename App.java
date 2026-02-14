import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class App {

    static DatagramSocket socket;
    static int localPort = 991;
    static int targetPort = 991;
    static String targetIP = "";

    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("Who's IP do you want to chat with?");
        targetIP = s.next();
        // System.out.println(
        //     "What local port would you like to receive messages on?"
        // );
        // localPort = s.nextInt();
        // System.out.println(
        //     "What port would your contact like to receive messages on?"
        // );
        // targetPort = s.nextInt();

        socket = new DatagramSocket(localPort);

        new Thread(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(
                    AudioConfig.getFormat()
                );

                line.open(AudioConfig.getFormat());
                line.start();

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length
                );

                System.out.println("Listening on " + localPort + "...");

                while (true) {
                    socket.receive(packet);
                    line.write(packet.getData(), 0, packet.getLength());
                    System.out.println(packet.getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
            .start();

        Thread.sleep(2000);
        System.out.println("Starting to stream file...");

        byte[] buffer = new byte[1024];

        var senderSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(targetIP);

        TargetDataLine mic = AudioConfig.getMicrophone();

        while (true) {
            int bytesRead = mic.read(buffer, 0, buffer.length);

            DatagramPacket packet = new DatagramPacket(
                buffer,
                bytesRead,
                address,
                targetPort
            );
            senderSocket.send(packet);
            System.out.println("sent packet: " + packet);
        }
    }
}
