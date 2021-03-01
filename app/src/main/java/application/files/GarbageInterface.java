package application.files;

@SuppressWarnings("ALL")
interface GarbageInterface {
    void createNewFallingGarbageTask();
    void disableGarbageTimer();
    void startFallingGarbage();
    void resetGarbage();
    void setGarbageLandedToFalse();
    void setListener(GarbageListener listener);
    boolean isGarbageLanded();
}
