package gameStates;

import CSCU9N6Library.Sound;
import helperClasses.*;
import renderableObjects.BackgroundEntity;
import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;
import renderableObjects.GameButton;
import soundsAndMusic.MIDIPlayer;
import spaceShipGame.SpaceshipGame;

import javax.sound.sampled.AudioInputStream;

import static helperClasses.GameObjects.ERenderLayer.*;


public class MainMenuState implements IGameState
{
    private GameObjects gameObjects;
    private StarFieldGenerator starfieldGenerator = new StarFieldGenerator();
    private EntityUpdateFactory updateFactory;
    private MIDIPlayer backgroundMusic;
    private UserInputHandler inputHandler;
    private SpaceshipGame spaceshipGame;

    private GameButton levelOneButton;
    private GameButton levelTwoButton;
    private GameButton levelThreeButton;

    private long millisInState = 0;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    public MainMenuState(SpaceshipGame spaceshipGame)
    {
        //Capture input parameters.
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = this.spaceshipGame.getGameObjects();
        this.SCREEN_WIDTH = this.spaceshipGame.getScreenWidth();
        this.SCREEN_HEIGHT = this.spaceshipGame.getScreenHeight();
        this.inputHandler = this.spaceshipGame.getUserInputHandler();
        this.updateFactory = this.spaceshipGame.getEntityUpdateFactory();

        //Set up the menu.
        this.gameObjects.addDrawable(getLaunchPad(), spaceStationLayer);

        this.levelOneButton = getLevel1Button();
        //TODO Other buttons.
        //this.levelTwoButton = getLevel1Button();
        //this.levelThreeButton = getLevel1Button();

        this.gameObjects.addDrawable(this.levelOneButton, UILayer);
        //TODO Other buttons.
        //this.gameObjects.addDrawable(this.levelTwoButton, UILayer);
        //this.gameObjects.addDrawable(this.levelThreeButton, UILayer);


        //Start playing background music.
        this.backgroundMusic = new MIDIPlayer("sounds/theme.mid");
        //this.backgroundMusic.setSoloTrack(6, true);
        this.gameObjects.addSound(this.backgroundMusic);
    }

    public EntityUpdate getUpdate(long millisSinceLastUpdate)
    {
        this.millisInState = this.millisInState + millisSinceLastUpdate;

        performUpdates();

        this.updateFactory.setMillisSinceLastUpdate(millisSinceLastUpdate);
        EntityUpdate update = this.updateFactory.getEntityUpdate();

        this.gameObjects.addDrawable(starfieldGenerator.spawnBackgroundStars(update, 1.1f), starFieldLayer1);
        this.gameObjects.addDrawable(starfieldGenerator.spawnBackgroundStars(update, 1.0f), starFieldLayer2);
        this.gameObjects.addDrawable(starfieldGenerator.spawnBackgroundStars(update, 0.9f), starFieldLayer3);

        return update;
    }

    private BackgroundEntity getLaunchPad()
    {
        BackgroundEntity launchPad = new BackgroundEntity(SCREEN_WIDTH, SCREEN_HEIGHT);
        Animation animation = new Animation();
        animation.setLoop(true);
        animation.loadAnimationFromSheet("images/launchPad.png", 1, 1, 10_000);
        Sprite sprite =  new Sprite(animation);

        launchPad.setSprite(sprite);
        launchPad.setSelfDestructBoundaries(
                SCREEN_WIDTH,
                -SCREEN_WIDTH,
                SCREEN_HEIGHT,
                -SCREEN_HEIGHT);

        return launchPad;
    }

    private GameButton getLevel1Button()
    {
        Sprite pressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1Button");
        Sprite unPressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1ButtonPressed");
        GameButton button = new GameButton(SCREEN_WIDTH, SCREEN_HEIGHT, pressedSprite, unPressedSprite, this.inputHandler, new ButtonOneFunctionObject(this));
        button.setXCoord(260.0f);
        button.setYCoord(200.0f);

        return button;
    }

    private void performUpdates()
    {
        if (this.millisInState > 6_000)
        {
            this.updateFactory.setSpaceshipXSpeed(0.0f);
            this.updateFactory.setSpaceshipYSpeed(-0.1f);
        }
    }

    private void buttonOneFunction()
    {
        this.spaceshipGame.loadNewGameState(new LevelOneGameState(this.spaceshipGame));

        this.updateFactory.setSpaceshipXSpeed(-0.5f);
        this.updateFactory.setSpaceshipYSpeed(0.0f);

        this.gameObjects.clearForeground(-0.5f, 0.0f);

        this.gameObjects.addSound(new Sound("sounds/buttonClick.wav"));
        this.backgroundMusic.stopPlaying();
    }

    private class ButtonOneFunctionObject implements IButtonFunctionObject
    {
        MainMenuState mainMenuState;

        ButtonOneFunctionObject(MainMenuState mainMenuState)
        {
            this.mainMenuState = mainMenuState;
        }

        @Override
        public void onButtonPress()
        {
            this.mainMenuState.buttonOneFunction();
        }
    }
}