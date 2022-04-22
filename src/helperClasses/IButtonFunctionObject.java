package helperClasses;

/**
 * A simple interface to allow button objects to be passed internal objects (which act as function pointers).
 */
public interface IButtonFunctionObject
{
    void onButtonPress();
}
