package levelEvents;

public interface ILevelEvent
{
    void setCurrentTime(long millisInState);

    void setNewTargetTime(long targetTimeInMillis);

    boolean isReadyToSelfDestruct();

    void cancel();
}
