package gameStates;

import helperClasses.EntityUpdate;
import levelEvents.ILevelEvent;

public interface IGameState
{
    EntityUpdate getUpdate(long millisSinceLastUpdate);

    void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers);
}