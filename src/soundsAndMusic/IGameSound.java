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
