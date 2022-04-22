//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package gameStates;

import CSCU9N6Library.Sound;
import factories.EntityUpdateFactory;
import factories.LevelEventFactory;
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

/**
 * A game state for the main menu.  Presents the player with three buttons, one for each level of the game.
 */
public class MainMenuState implements IGameState
{
    private GameObjects gameObjects;
    private StarFieldGenerator starfieldGenerator;
    private EntityUpdateFactory updateFactory;
    private MIDIPlayer backgroundMusic;
    private UserInputHandler inputHandler;
    private SpaceshipGame spaceshipGame;
    private LevelEventFactory eventFactory;

    private GameButton levelOneButton;
    private GameButton levelTwoButton;
    private GameButton levelThreeButton;
    private boolean paused = false;

    private long millisInState = 0;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private LinkedList<ILevelEvent> levelEvents = new LinkedList<>();
    private LinkedList<ILevelEvent> levelEventsToAdd = new LinkedList<>();

    /**
     * The constructor.
     * @param spaceshipGame A SpaceshipGame object to interrogate for needed data.
     */
    public MainMenuState(SpaceshipGame spaceshipGame)
    {
        //Capture input parameters.
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = this.spaceshipGame.getGameObjects();
        this.SCREEN_WIDTH = this.spaceshipGame.getScreenWidth();
        this.SCREEN_HEIGHT = this.spaceshipGame.getScreenHeight();
        this.inputHandler = this.spaceshipGame.getUserInputHandler();
        this.updateFactory = this.spaceshipGame.getEntityUpdateFactory();
        this.eventFactory = new LevelEventFactory(this.spaceshipGame);
        this.starfieldGenerator = new StarFieldGenerator(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);

        //Set up the menu.

        this.levelOneButton = getLevel1Button();
        this.levelTwoButton = getLevel2Button();
        this.levelThreeButton = getLevel3Button();

        //Start playing background music.
        this.backgroundMusic = new MIDIPlayer("sounds/theme.mid");
        //this.backgroundMusic.setSoloTrack(6, true);
        this.gameObjects.addSound(this.backgroundMusic);

        setUpLevelEvents();
    }

    /**
     * Safely adds a level event to the game state.
     * @param event
     * @param millisUntilEventTriggers
     */
    @Override
    public void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers)
    {
        event.setNewTargetTime(this.millisInState + millisUntilEventTriggers);  //Add the current time to the countdown timer.
        this.levelEventsToAdd.add(event);
    }

    /**
     * Creates and adds the level events for this game state.
     */
    private void setUpLevelEvents()
    {
        this.eventFactory.setGameState(this);

        eventFactory.addDisplayDrawableEvent(getLaunchPad(), spaceStationLayer, 0);
        eventFactory.addPlaySoundEvent(new MIDIPlayer("theme.mid"), 0);

        eventFactory.addDisplayDrawableEvent(this.levelOneButton, UILayer, 2_000);
        eventFactory.addDisplayDrawableEvent(this.levelTwoButton, UILayer, 2_000);
        eventFactory.addDisplayDrawableEvent(this.levelThreeButton, UILayer, 2_000);
        eventFactory.addShipManoeuvreEvent(0.0f, -0.1f, 2_000);
    }

    /**
     * Updates the list of level events, and safely adds and removes level events without causing concurrentModificationExceptions.
     */
    private void handleLevelEvents()
    {
        LinkedList<ILevelEvent> eventsToDelete = new LinkedList<>();

        //Do it this bloody stupid way because otherwise Java throws a concurrentModificationException.
        this.levelEvents.addAll(this.levelEventsToAdd);
        this.levelEventsToAdd.clear();

        for (ILevelEvent event : this.levelEvents)
        {
            if (event.isReadyToSelfDestruct())
            {
                eventsToDelete.add(event);
            }
            else
            {
                event.updateCurrentTime(this.millisInState);
            }
        }

        this.levelEvents.removeAll(eventsToDelete);
    }

    /**
     * Gets an EntityUpdate objects which can be used to update the rest of the game system.
     * @param millisSinceLastUpdate A long which is the time since the last update
     * @return  An EntityUpdate object which contains the relevant information.
     */
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

    /**
     * Creates a "launch pad" background entity and adds it to the GameObject collection.
     * @return  A BackgroundEntity which is a launch pad to be rendered behind the space ship.
     */
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

    /**
     * Creates and returns the button which will take the player to level one.
     * @return  A GameButton object which contains the appropriate ButtonFunctionObject
     */
    private GameButton getLevel1Button()
    {
        Sprite pressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1Button");
        Sprite unPressedSprite = SpriteFactory.getSpriteFromPNGFile("Level1ButtonPressed");
        GameButton button = new GameButton(SCREEN_WIDTH, SCREEN_HEIGHT, pressedSprite, unPressedSprite, this.inputHandler, new ButtonOneFunctionObject(this, this.spaceshipGame));
        button.setXCoord(260.0f);
        button.setYCoord(200.0f);

        return button;
    }

    /**
     * Creates and returns the button which will take the player to level two.
     * @return  A GameButton object which contains the appropriate ButtonFunctionObject
     */
    private GameButton getLevel2Button()
    {
        Sprite pressedSprite = SpriteFactory.getSpriteFromPNGFile("Level2Button");
        Sprite unPressedSprite = SpriteFactory.getSpriteFromPNGFile("Level2ButtonPressed");
        GameButton button = new GameButton(SCREEN_WIDTH, SCREEN_HEIGHT, pressedSprite, unPressedSprite, this.inputHandler, new ButtonTwoFunctionObject(this, this.spaceshipGame));
        button.setXCoord(260.0f);
        button.setYCoord(400.0f);

        return button;
    }

    /**
     * Creates and returns the button which will take the player to level three.
     * @return  A GameButton object which contains the appropriate ButtonFunctionObject
     */
    private GameButton getLevel3Button()
    {
        Sprite pressedSprite = SpriteFactory.getSpriteFromPNGFile("Level3Button");
        Sprite unPressedSprite = SpriteFactory.getSpriteFromPNGFile("Level3ButtonPressed");
        GameButton button = new GameButton(SCREEN_WIDTH, SCREEN_HEIGHT, pressedSprite, unPressedSprite, this.inputHandler, new ButtonThreeFunctionObject(this, this.spaceshipGame));
        button.setXCoord(260.0f);
        button.setYCoord(600.0f);

        return button;
    }

    /**
     * A class which can be passed in to a button, and which allows the button to reference back to this object and call a particular function.
     * In this case, the function it to start level one.
     */
    private static class ButtonOneFunctionObject implements IButtonFunctionObject
    {
        MainMenuState mainMenuState;
        SpaceshipGame spaceshipGame;

        /**
         * The constructor.
         * @param mainMenuState The MainMenuState object to call a function in.
         * @param spaceshipGame The SpaceShipGame object to interrogate for needed data.
         */
        ButtonOneFunctionObject(MainMenuState mainMenuState, SpaceshipGame spaceshipGame)
        {
            this.mainMenuState = mainMenuState;
            this.spaceshipGame = spaceshipGame;
        }

        /**
         * Adds two level events to the MainMenuState, which set up the appropriate level.
         */
        @Override
        public void onButtonPress()
        {
            this.mainMenuState.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.levelOne), 500);
            this.mainMenuState.addLevelEvent(new PlaySound(new Sound("sounds/buttonClick.wav"), this.spaceshipGame.getGameObjects()), 0);
        }
    }

    /**
     * A class which can be passed in to a button, and which allows the button to reference back to this object and call a particular function.
     * In this case, the function it to start level two.
     */
    private static class ButtonTwoFunctionObject implements IButtonFunctionObject
    {
        MainMenuState mainMenuState;
        SpaceshipGame spaceshipGame;

        /**
         * The constructor.
         * @param mainMenuState The MainMenuState object to call a function in.
         * @param spaceshipGame The SpaceShipGame object to interrogate for needed data.
         */
        ButtonTwoFunctionObject(MainMenuState mainMenuState, SpaceshipGame spaceshipGame)
        {
            this.mainMenuState = mainMenuState;
            this.spaceshipGame = spaceshipGame;
        }

        /**
         * Adds two level events to the MainMenuState, which set up the appropriate level.
         */
        @Override
        public void onButtonPress()
        {
            this.mainMenuState.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.levelTwo), 500);
            this.mainMenuState.addLevelEvent(new PlaySound(new Sound("sounds/buttonClick.wav"), this.spaceshipGame.getGameObjects()), 0);
        }
    }

    /**
     * A class which can be passed in to a button, and which allows the button to reference back to this object and call a particular function.
     * In this case, the function it to start level three.
     */
    private static class ButtonThreeFunctionObject implements IButtonFunctionObject
    {
        MainMenuState mainMenuState;
        SpaceshipGame spaceshipGame;

        /**
         * The constructor.
         * @param mainMenuState The MainMenuState object to call a function in.
         * @param spaceshipGame The SpaceShipGame object to interrogate for needed data.
         */
        ButtonThreeFunctionObject(MainMenuState mainMenuState, SpaceshipGame spaceshipGame)
        {
            this.mainMenuState = mainMenuState;
            this.spaceshipGame = spaceshipGame;
        }

        /**
         * Adds two level events to the MainMenuState, which set up the appropriate level.
         */
        @Override
        public void onButtonPress()
        {
            this.mainMenuState.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.levelThree), 500);
            this.mainMenuState.addLevelEvent(new PlaySound(new Sound("sounds/buttonClick.wav"), this.spaceshipGame.getGameObjects()), 0);
        }
    }
}