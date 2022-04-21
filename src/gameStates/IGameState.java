package gameStates;

import helperClasses.EntityUpdate;
import levelEvents.ILevelEvent;

/**
 * An interface to allow the different game states to be easily switched between.
 */
public interface IGameState
{
    /**
     * Get a new update data object from the GameState
     * @param millisSinceLastUpdate A long which is the time since the last update
     * @return  An EntityUpdate class which contains the data for this update
     */
    EntityUpdate getUpdate(long millisSinceLastUpdate);

    void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers);
}