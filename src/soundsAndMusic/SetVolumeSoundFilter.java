package soundsAndMusic;

import CSCU9N6Library.ISoundFilter;

public class SetVolumeSoundFilter implements ISoundFilter
{
    private float volume;

    public SetVolumeSoundFilter(float volume)
    {
        this.volume = volume;
    }

    @Override
    public short getAmplitude(short amplitude, int numberOfBytesFromStream)
    {
        return (short) ((float) amplitude * this.volume);
    }
}
