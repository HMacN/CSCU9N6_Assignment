package factories;

import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;

/**
 * A factory with a few static methods to allow easy creation of sprites elsewhere in the project.
 * More of a collection of related methods, than a factory per se.
 */
public class SpriteFactory
{
    /**
     * Generate a new sprite object from a given filename.  Only renders the first frame of a sprite sheet.
     * @param filename  A string which is the name of the file to get a sprite sheet from
     * @return  A Sprite object made from the given file
     */
    public static Sprite getSpriteFromPNGFile(String filename)
    {
        Animation animation = new Animation();
        animation.loadAnimationFromSheet("images/" + filename + ".png", 1, 1, 10_000);
        animation.setLoop(true);

        return new Sprite(animation);
    }

    /**
     * Generate a new sprite object from a given filename.  Will create an animated sprite.
     * @param filename  A string which is the name of the file to generate a sprite from
     * @param rows  An int which is the number of rows in the spritesheet
     * @param columns   An int which is the number of columns in the sprite sheet
     * @param frameDuration An int which is the time between frames in the animation
     * @return  A Sprite object from the given spritesheet
     */
    public static Sprite getSpriteFromPNGFile(String filename, int rows, int columns, int frameDuration)
    {
        Animation animation = new Animation();
        animation.loadAnimationFromSheet("images/" + filename + ".png", columns, rows, frameDuration);
        animation.setLoop(true);

        return new Sprite(animation);
    }
}
