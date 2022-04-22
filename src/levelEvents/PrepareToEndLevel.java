package levelEvents;

import gameStates.EGameState;
import spaceShipGame.SpaceshipGame;

/**
 * A level event which notifies the level's parent SpaceShipGame object that it needs to start tearing down the GameState.
 * Does not actually end the level, just starts the process.
 */
public class PrepareToEndLevel implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private SpaceshipGame spaceshipGame;
    private EGameState nextState;

    /**
     * The constructor.
     * @param spaceshipGame A SpaceShipGame object which is to be notified that it needs to start changing the game state.
     * @param nextState An enum which describes the game state to be loaded after the current one.
     */
    public PrepareToEndLevel(SpaceshipGame spaceshipGame, EGameState nextState)
    {
        this.spaceshipGame = spaceshipGame;
        this.nextState = nextState;
    }

    /**
     * Notifies this event of how much time has elapsed in the current game state.
     * @param millisInState A long which is the elapsed time that the game has been in its current state.
     */
    @Override
    public void updateCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.spaceshipGame.prepareToLoadNewGameState(this.nextState);
            this.selfDestruct = true;
        }
    }

    /**
     * Resets the target time for this event.  Note that setting it to before the current time will cause the event to trigger.
     * @param targetTimeInMillis    A long which is the new target time for the event.
     */
    @Override
    public void setNewTargetTime(long targetTimeInMillis)
    {
        this.targetTimeInMillis = targetTimeInMillis;
    }

    /**
     * A getter for the self destruct flag.  Used to determine whether or not to remove the event rather than update it.
     * @return
     */
    @Override
    public boolean isReadyToSelfDestruct()
    {
        return this.selfDestruct;
    }

    /**
     * A setter for the self-destruct flag.  Also sets the target time to a very high number to help ensure that the event does not accidentally trigger.
     */
    @Override
    public void cancel()
    {
        this.selfDestruct = true;
        this.targetTimeInMillis = 999_999_999;
    }
}
