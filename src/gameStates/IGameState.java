package gameStates;

import helperClasses.EntityUpdate;

public interface IGameState
{
    EntityUpdate getUpdate(long millisSinceLastUpdate);
}