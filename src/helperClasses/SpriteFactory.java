package helperClasses;

import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;

public class SpriteFactory
{
    public static Sprite getSpriteFromPNGFile(String filename)
    {
        Animation animation = new Animation();
        animation.loadAnimationFromSheet("images/" + filename + ".png", 1, 1, 10_000);
        animation.setLoop(true);

        return new Sprite(animation);
    }

    public static Sprite getSpriteFromPNGFile(String filename, int rows, int columns, int frameDuration)
    {
        Animation animation = new Animation();
        animation.loadAnimationFromSheet("images/" + filename + ".png", columns, rows, frameDuration);
        animation.setLoop(true);

        return new Sprite(animation);
    }
}
