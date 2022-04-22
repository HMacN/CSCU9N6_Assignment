package soundsAndMusic;

import javax.sound.midi.*;
import java.io.File;

/**
 * A class which plays MIDI files.
 */
public class MIDIPlayer extends Thread implements IGameSound
{
    private String filename;
    private boolean finished = false;
    private Sequencer sequencer;
    private long startingTick;

    /**
     * The constructor
     * @param filename A String which is the file to play.
     */
    public MIDIPlayer(String filename)
    {
        this.filename = filename;
    }

    /**
     * Starts the track playing.
     */
    @Override
    public void run()
    {
        try
        {
            // Get a reference to the MIDI data stored in the file
            Sequence score = MidiSystem.getSequence(new File(this.filename));
            // Get a reference to a sequencer that will play it
            this.sequencer = MidiSystem.getSequencer();

            // Open the sequencer and play the sequence (score)
            sequencer.open();
            sequencer.setSequence(score);
            sequencer.setTickPosition(this.startingTick);
            sequencer.start();
            while (sequencer.isRunning() && !this.finished) //If "finished", stop the track.
            {
                Thread.sleep(100);
            }
            sequencer.close();
            this.finished = true;
        }
        catch (Exception exception)
        {
            System.out.println("The MIDI Player encountered a problem.");
            System.out.println(exception);
        }
    }

    /**
     * A getter for the "finished" flag.
     * @return  A boolean which indicates whether or not this sound is still playing.
     */
    @Override
    public boolean getFinished()
    {
        return this.finished;
    }

    /**
     * Starts this sound playing.  Thread issues mean that MIDI files sometimes take a few seconds to start.
     */
    @Override
    public void play()
    {
        this.start();
    }

    /**
     * Stops this sound from playing by setting the "finished" flag early.
     */
    @Override
    public void finishPlaying()
    {
        this.finished = true;
    }
}
