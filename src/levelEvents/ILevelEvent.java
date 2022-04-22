//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package levelEvents;

/**
 * An interface for events which can be loaded in to levels.  These events are dormant until the time given to them exceeds the "target time" for the event.
 * They then perform whatever function they are meant for, and then set their self destruct flag to true, so that they can be removed before they are updated again.
 */
public interface ILevelEvent
{
    /**
     * Tells the event what the current time is, so that it can decide whether or not to activate.
     * @param millisInState A long which is the elapsed time that the game has been in its current state.
     */
    void updateCurrentTime(long millisInState);

    /**
     * Changes the target time for the event.  If this is changed to less than the current time, the event will trigger on the next update.
     * @param targetTimeInMillis    A long which is the new target time for the event.
     */
    void setNewTargetTime(long targetTimeInMillis);

    /**
     * A getter for the self destruct flag for this event.
     * @return  A boolean which describes whether or not to remove this event.
     */
    boolean isReadyToSelfDestruct();

    /**
     * Sets this event to self-destruct.
     */
    void cancel();
}
