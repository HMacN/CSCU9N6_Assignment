import game2D.*;

import java.util.ArrayList;

public interface IGameState
{
    void update(long elapsedTime);

    void initialSetup();
}