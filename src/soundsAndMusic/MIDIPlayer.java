package soundsAndMusic;

import javax.sound.midi.*;
import java.io.File;

public class MIDIPlayer extends Thread implements IGameSound
{
    private String filename;
    private boolean finished = false;
    private Sequencer sequencer;
    private long startingTick;

    public MIDIPlayer(String filename)
    {
        this.filename = filename;
    }

    public void setStartingTick(long tick)
    {
        this.startingTick = tick;
    }

    public void setSoloTrack(int track, boolean isSolo)
    {
        this.sequencer.setTrackSolo(track, isSolo);
    }

    public void stopPlaying()
    {
        //Handle the music not having started when instructed to stop.
        if (sequencer == null)
        {
            System.out.println("MIDI Player encountered a problem: No music playing when instructed to stop.");
        }
        else
        {
            sequencer.close();
        }

        this.finished = true;
    }

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
            while (sequencer.isRunning() && !this.finished)
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

    @Override
    public boolean getFinished()
    {
        return this.finished;
    }

    @Override
    public void play()
    {
        this.start();
    }

    @Override
    public void finishPlaying()
    {
        this.finished = true;
    }
}
