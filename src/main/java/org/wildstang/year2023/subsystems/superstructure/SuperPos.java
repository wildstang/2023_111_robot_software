package org.wildstang.year2023.subsystems.superstructure;

public enum SuperPos {

    SCORE_HIGH       (69,245.4,117.2,69,245.4,117.2,0,true),
    SCORE_MID        (32.2,233.4,97.9,32.2,233.4,97.9,0,true),
    //SCORE_LOW        (0,231,77,0,231,77,0,true), // old score low position
    SCORE_LOW        (14, 250, 80, 14, 250, 80, 0, true),
    AUTO_HIGH        (40, 230, 180, 40, 230, 180, 0, true),
    NEUTRAL          (0,195,195,0,195,195,0,false),
    STOWED           (0, 215, 260, 0, 215, 260, 0,false),
    INTAKE_FRONT     (11,288,155,11,288,155,0,true),
    //HP_STATION_DOUBLE(24.8,203,85,22,203,83,1,true), // old double substation position
    HP_STATION_DOUBLE(20, 240, 170, 20, 240, 170, 1,true),
    HP_STATION_SINGLE(0,90,90,0,90,90,0,false),
    INTAKE_BACK      (35,56.4,186.4,28.7,56.4,192.4,2,false),
    INTAKE_BACK_LOW  (26, 56.4, 192.4, 28.7, 56.4, 187, 3,false),
    PRETHROW         (0, 170, 170, 0, 170, 170, 0,false);

    public final double cubeL;
    public final double cubeA;
    public final double cubeW;
    public final double coneL;
    public final double coneA;
    public final double coneW;
    public final int liftMod;
    public final boolean isForward;

    private SuperPos(double coneL, double coneA, double coneW, double cubeL, double cubeA, double cubeW, int liftMod, boolean isForward){
        this.cubeL = cubeL;
        this.cubeA = cubeA;
        this.cubeW = cubeW;
        this.coneL = coneL;
        this.coneA = coneA;
        this.coneW = coneW;
        this.liftMod = liftMod;
        this.isForward = isForward;
    }

    public double getL(boolean gamepiece){
        return gamepiece == SuperConts.CONE ? coneL : cubeL;
    }
    public double getA(boolean gamepiece){
        return gamepiece == SuperConts.CONE ? coneA : cubeA;
    }
    public double getW(boolean gamepiece){
        return gamepiece == SuperConts.CONE ? coneW : cubeW;
    }
    public boolean getDirection(){
        return isForward;
    }   
    public int getLiftMod(){
        return liftMod;
    }
}