//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package soundsAndMusic;

/**
 * An interface which allows both .wav sounds and .mid music tracks to be treated as the same thing.
 */
public interface IGameSound
{
    /**
     * A getter for the "finished" flag.
     * @return  A boolean which indicates whether or not this sound is still playing.
     */
    boolean getFinished();

    /**
     * Starts this sound playing.  Thread issues mean that MIDI files sometimes take a few seconds to start.
     */
    void play();

    /**
     * Stops this sound from playing.
     */
    void finishPlaying();
}
