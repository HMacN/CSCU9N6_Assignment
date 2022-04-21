package gameStates;

import factories.EntityUpdateFactory;
import factories.SpriteFactory;
import helperClasses.*;
import levelEvents.*;
import renderableObjects.BackgroundEntity;
import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;
import renderableObjects.GameButton;
import soundsAndMusic.MIDIPlayer;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

import java.util.LinkedList;

import static spaceShipGame.GameObjects.ERenderLayer.*;


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
    private boolean paused = false;

    private long millisInState = 0;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private LinkedList<ILevelEvent> levelEvents = new LinkedList<>();
    private LinkedList<ILevelEvent> levelEventsToAdd = new LinkedList<>();

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
        //this.gameObjects.addDrawable(getLaunchPad(), spaceStationLayer);

        this.levelOneButton = getLevel1Button();
        //TODO Other buttons.
        //this.levelTwoButton = getLevel1Button();
        //this.levelThreeButton = getLevel1Button();

        //this.gameObjects.addDrawable(this.levelOneButton, UILayer);
        //TODO Other buttons.
        //this.gameObjects.addDrawable(this.levelTwoButton, UILayer);
        //this.gameObjects.addDrawable(this.levelThreeButton, UILayer);

        //Start playing background music.
        this.backgroundMusic = new MIDIPlayer("sounds/theme.mid");
        //this.backgroundMusic.setSoloTrack(6, true);
        this.gameObjects.addSound(this.backgroundMusic);

        addLevelEvents();
    }

    @Override
    public void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers)
    {
        event.setNewTargetTime(this.millisInState + millisUntilEventTriggers);  //Add the current time to the countdown timer.
        this.levelEventsToAdd.add(event);
    }

    private void addLevelEvents()
    {
        addLevelEvent(new DisplayDrawable(getLaunchPad(), this.gameObjects, spaceStationLayer), 0);
        addLevelEvent(new PlaySound(new MIDIPlayer("theme.mid"), this.gameObjects), 0);

        addLevelEvent(new DisplayDrawable(this.levelOneButton, this.gameObjects, UILayer), 2_000);
        addLevelEvent(new ShipManoeuvre(0.0f, -0.1f, this.updateFactory), 2_000);
    }

    private void handleLevelEvents()
    {
        LinkedList<ILevelEvent> eventsToDelete = new LinkedList<>();

        //Do it this bloody stupid way because otherwise Java throws a concurrentModificationException.
        this.levelEvents.addAll(this.levelEventsToAdd);

        for (ILevelEvent event : this.levelEvents)
        {
            if (event.isReadyToSelfDestruct())
            {
                eventsToDelete.add(event);
            }
            else
            {
                event.setCurrentTime(this.millisInState);
            }
        }

        this.levelEvents.removeAll(eventsToDelete);
    }

    public EntityUpdate getUpdate(long millisSinceLastUpdate)
    {
        if (!this.paused)
        {
            this.millisInState = this.millisInState + millisSinceLastUpdate;
        }

        handleLevelEvents();

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

        return launchPad;
    }

    private GameButton getLevel1Button()
    {
        Sprite pressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1Button");
        Sprite unPressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1ButtonPressed");
        GameButton button = new GameButton(SCREEN_WIDTH, SCREEN_HEIGHT, pressedSprite, unPressedSprite, this.inputHandler, new ButtonOneFunctionObject(this, this.spaceshipGame));
        button.setXCoord(260.0f);
        button.setYCoord(200.0f);

        return button;
    }

    private static class ButtonOneFunctionObject implements IButtonFunctionObject
    {
        MainMenuState mainMenuState;
        SpaceshipGame spaceshipGame;

        ButtonOneFunctionObject(MainMenuState mainMenuState, SpaceshipGame spaceshipGame)
        {
            this.mainMenuState = mainMenuState;
            this.spaceshipGame = spaceshipGame;
        }

        @Override
        public void onButtonPress()
        {
            this.mainMenuState.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.levelOne), 500);
        }
    }
}