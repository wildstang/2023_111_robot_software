package org.wildstang.year2023.subsystems.superstructure;

public enum SuperPos {

    SCORE_HIGH(68,245.4,51.8,68,245.4,51.8,true),
    SCORE_MID(31.2,233.4,44.5,31.2,233.4,44.5,true),
    SCORE_LOW(0,270,135,0,270,135,true),
    NEUTRAL(0,180,180,0,180,180,false),
    INTAKE_FRONT(13.5,283,52,13.5,283,52,true),
    INTAKE_FRONT_LOW(0,283,52,0,283,52,true),
    HP_STATION_FRONT(25,203,58,25,203,58,true),
    HP_STATION_BACK(40,140,310,40,140,310,false),
    INTAKE_BACK(34.5,56.4,302,26.7,56.4,302,false);
    //upright cone back 26.7 56.4 302

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