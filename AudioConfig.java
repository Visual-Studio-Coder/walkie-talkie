import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioConfig {

    public static AudioFormat getFormat() {
        return new AudioFormat(44100.0f, 16, 1, true, false);
    }

    public static AudioInputStream getStream(String path)
        throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(new File(path));
    }

    public static TargetDataLine getMicrophone()
        throws LineUnavailableException {
        AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("microphone not supported");
            return null;
        }

        TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
        mic.open(format);
        mic.start();
        return mic;
    }
}
