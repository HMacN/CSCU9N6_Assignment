package gameStates;

import helperClasses.EntityUpdate;
import helperClasses.EntityUpdateFactory;

public interface IGameState
{
    EntityUpdate getUpdate(long millisSinceLastUpdate);
}