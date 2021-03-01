package application.files;

interface GarbageListener {
    void handleAvoidedGarbage(String avoidedGarbage);
    boolean handleHitPlayer(int x, int y,  String garbageType);
    void handleLoseLife();
    void emptyLifePointList();
    void emptyBigGarbageList();
}
