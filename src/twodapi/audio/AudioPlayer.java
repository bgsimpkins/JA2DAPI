/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.audio;

import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author bsimpkins
 */
public class AudioPlayer implements LineListener
{
    private HashMap<String,Clip> clipList = new HashMap();

    public void addSound(String filename)
    {
        try {
            // link an audio stream to the sound clip's file
            AudioInputStream stream = AudioSystem.getAudioInputStream(
                              getClass().getResource(filename) );

            AudioFormat format = stream.getFormat();

            // convert ULAW/ALAW formats to PCM format
            if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
               (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
                AudioFormat newFormat =
                   new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                        format.getSampleRate(),
                                        format.getSampleSizeInBits()*2,
                                        format.getChannels(),
                                        format.getFrameSize()*2,
                                        format.getFrameRate(), true);  // big endian
                // update stream and format details
                stream = AudioSystem.getAudioInputStream(newFormat, stream);
                System.out.println("Converted Audio format: " + newFormat);
                format = newFormat;
            }

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // make sure sound system supports data line
            if (!AudioSystem.isLineSupported(info))
            {
                System.out.println("Unsupported Clip File: " + filename);
                System.exit(0);
            }

            Clip clip = null;
            // get clip line resource
            clip = (Clip) AudioSystem.getLine(info);
            clipList.put(filename,clip);

            // listen to clip for events
            clip.addLineListener(this);

            clip.open(stream);    // open the sound file as a clip
            stream.close(); // we're done with the input stream  // new

            clip.setFramePosition(0);
            // clip.setMicrosecondPosition(0);

            //checkDuration();
        } // end of try block

        catch (UnsupportedAudioFileException audioException) {
        System.out.println("Unsupported audio file: " + filename);
        System.exit(0);
        }
        catch (LineUnavailableException noLineException) {
        System.out.println("No audio line available for : " + filename);
        System.exit(0);
        }
        catch (IOException ioException) {
        System.out.println("Could not read: " + filename);
        System.exit(0);
        }
        catch (Exception e) {
        System.out.println("Problem with " + filename);
        System.exit(0);
        }
    } 

    public void play(String filename)
    {
        Clip clip = clipList.get(filename);
        if (clip != null)
        {
            System.out.println("Playing "+filename);
            clip.start(); 
        }
    }

    public void stop(String filename)
    {
        Clip clip = clipList.get(filename);
        if (clip != null)
        {
            System.out.println("Stopping "+filename);
            clip.stop(); 
        }
    }

    public void update(LineEvent lineEvent)
    {
        // has the clip has reached its end?
        if (lineEvent.getType() == LineEvent.Type.STOP)
        {

            if (!(lineEvent instanceof Clip)) return;

            Clip clip = (Clip)lineEvent.getLine();

            System.out.println("Exiting...");
            clip.stop();
            clip.setFramePosition(0);

            lineEvent.getLine().close();
            System.exit(0);
        }
    }

    public static void main(String[] args)
    {
        AudioPlayer ap = new AudioPlayer();

        ap.addSound("/sounds/music.wav");
        ap.play("/sounds/music.wav");
    }
}
