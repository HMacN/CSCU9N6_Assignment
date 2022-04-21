package CSCU9N6Library;

import soundsAndMusic.IGameSound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;

public class Sound extends Thread implements IGameSound
{

    private final String filename;    // The name of the file to play
    private boolean finished;    // A flag showing that the thread has finished
    private AdjustableFilterInputStream filterInputStream;
    private ISoundFilter filter = null;
    private AudioInputStream stream;
    private AudioFormat format;

    public Sound(String fileName)
    {
        this.filename = fileName;
        this.finished = false;


		File file = new File(this.filename);
		try
		{
			this.stream = AudioSystem.getAudioInputStream(file);
		}
		catch (UnsupportedAudioFileException | IOException e)
		{
			e.printStackTrace();
		}
		this.format = this.stream.getFormat();
    }

    /**
     * run will play the actual sound but you should not call it directly.
     * You need to call the 'start' method of your sound object (inherited
     * from Thread, you do not need to declare your own). 'run' will
     * eventually be called by 'start' when it has been scheduled by
     * the process scheduler.
     */
    @Override
    public void run()
    {
        try
        {
            DataLine.Info info = new DataLine.Info(Clip.class, this.format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(this.stream);
            clip.start();
            Thread.sleep(100);
            while (clip.isRunning() && !this.finished)
            {
                Thread.sleep(100);
            }
            clip.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        this.finished = true;
    }

    public void setFilter(ISoundFilter filter)
    {
        this.filter = filter;

		this.filterInputStream = new AdjustableFilterInputStream(this.stream);
		this.filterInputStream.setFilter(this.filter);
		this.stream = new AudioInputStream(this.filterInputStream, this.format, this.stream.getFrameLength());
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

    private class AdjustableFilterInputStream extends FilterInputStream
    {
        private ISoundFilter filter;

        /**
         * Creates a <code>FilterInputStream</code>
         * by assigning the  argument <code>in</code>
         * to the field <code>this.in</code> so as
         * to remember it for later use.
         *
         * @param stream the underlying input stream, or <code>null</code> if
         *               this instance is to be created without an underlying stream.
         */
        protected AdjustableFilterInputStream(AudioInputStream stream)
        {
            super(stream);
        }

        public void setFilter(ISoundFilter filter)
        {
            this.filter = filter;
        }

        public short get16BitSample(byte[] buffer, int position)
        {
            //& 0xff gives the least significant byte (8 bits).
            return (short) (((buffer[position + 1] & 0xff) << 8) |
                    (buffer[position] & 0xff));
        }

        public void set16BitSample(byte[] buffer, int position, short sample)
        {
            buffer[position] = (byte) (sample & 0xFF);
            buffer[position + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        public int read(byte[] sample, int offset, int length)
                throws IOException
        {
            //Check if there is a filter.
            if (this.filter == null)
            {
                //Do nothing if there is no filter set.
                return super.read(sample, offset, length);
            }
            else
            {
                int numberOfBytesFromStream = super.read(sample, offset, length);
                short amplitude;

                // Loop through the sample 2 bytes at a time
                for (int p = 0; p < numberOfBytesFromStream; p = p + 2)
                {
                    amplitude = get16BitSample(sample, p);
                    amplitude = this.filter.getAmplitude(amplitude, numberOfBytesFromStream);
                    set16BitSample(sample, p, amplitude);
                }
                return length;
            }
        }
    }
}
