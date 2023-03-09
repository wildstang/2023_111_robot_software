package org.wildstang.year2023.subsystems.superstructure;

public enum SuperPos {

    SCORE_HIGH       (69,245.4,51.8,69,245.4,51.8,true),
    SCORE_MID        (32.2,233.4,44.5,32.2,233.4,44.5,true),
    SCORE_LOW        (0,231,26,0,231,26,true),
    NEUTRAL          (0,195,180,0,195,180,false),
    STOWED           (0, 215, 225, 0, 215, 225, false),
    INTAKE_FRONT     (13.5,283,52,13.5,283,52,true),
    INTAKE_FRONT_LOW (0,283,52,0,283,52,true),
    HP_STATION_DOUBLE(26,203,62,19,203,60,true),
    HP_STATION_SINGLE(40,70,180,40,70,180,false),
    INTAKE_BACK      (33,56.4,310,28.7,56.4,316,false),
    INTAKE_BACK_LOW  (26, 56.4, 316.0, 28.7, 56.4, 310.6, false),
    PRETHROW         (0, 170, 180, 0, 170, 180, false);

    public final double cubeL;
    public final double cubeA;
    public final double cubeW;
    public final double coneL;
    public final double coneA;
    public final double coneW;
    public final boolean isForward;

    private SuperPos(double coneL, double coneA, double coneW, double cubeL, double cubeA, double cubeW, boolean isForward){
        this.cubeL = cubeL;
        this.cubeA = cubeA;
        this.cubeW = cubeW;
        this.coneL = coneL;
        this.coneA = coneA;
        this.coneW = coneW;
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
}