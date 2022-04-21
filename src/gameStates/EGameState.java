package gameStates;

public enum EGameState
{
    mainMenu,
    levelOne,
    levelTwo,
    levelThree,
    undefinedState;

    public boolean matches(IGameState unknownState)
    {
        EGameState stateFound = undefinedState;

        if (unknownState.getClass().equals(MainMenuState.class))
        {
            stateFound = mainMenu;
        }
        else if (unknownState.getClass().equals((LevelOneGameState.class)))
        {
            stateFound = levelOne;
        }

        if(stateFound == this)
        {
            return true;
        }

        return false;
    }
}
