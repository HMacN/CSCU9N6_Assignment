package soundsAndMusic;

import CSCU9N6Library.ISoundFilter;

public class FadeSoundFilter implements ISoundFilter
{
    private float volume = 1.0f;
    private float change = -1.0f;

    @Override
    public short getAmplitude(short amplitude, int numberOfBytesFromStream)
    {
        if (this.change < 0.0)
        {
            this.change = 2.0f * (1.0f / (float) numberOfBytesFromStream);
        }

        this.volume = this.volume - this.change;
        return (short) ((float) amplitude * this.volume);
    }
}
