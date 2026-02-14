import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class App {

    static DatagramSocket socket;
    static int localPort = 991;
    static int targetPort = 991;
    static String targetIP = "127.0.0.1";

    public static void main(String[] args) throws Exception {
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
        InetAddress address = InetAddress.getLoopbackAddress();

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
