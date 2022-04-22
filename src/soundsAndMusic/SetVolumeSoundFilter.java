package soundsAndMusic;

import CSCU9N6Library.ISoundFilter;

/**
 * A sound filter which adjusts the volume of the sound being played.
 */
public class SetVolumeSoundFilter implements ISoundFilter
{
    private float volume;

    /**
     * The Constructor
     * @param volume    A float which is the factor to adjust the sound amplitude by.
     */
    public SetVolumeSoundFilter(float volume)
    {
        this.volume = volume;
    }

    /**
     * A getter for the sound amplitude.
     * @param amplitude A short which is an amplitude to put through the filter.
     * @param numberOfBytesFromStream   An int which is the length of the amplitude in the stream.
     * @return  A short which is the new amplitude.
     */
    @Override
    public short getAmplitude(short amplitude, int numberOfBytesFromStream)
    {
        return (short) ((float) amplitude * this.volume);
    }
}
