package org.wildstang.year2023.subsystems.superstructure;

public enum SuperPos {

    SCORE_HIGH(50,270,135,50,270,135,true),
    SCORE_MID(20,270,135,20,270,135,true),
    SCORE_LOW(0,270,135,0,270,135,true),
    NEUTRAL(0,180,180,0,180,180,false),
    INTAKE_FRONT(0,270,45,0,270,45,true),
    INTAKE_FRONT_LOW(0,270,45,0,270,45,true),
    HP_STATION_FRONT(0,180,90,0,180,90,true),
    HP_STATION_BACK(0,180,270,0,180,270,false),
    INTAKE_BACK(0,90,315,0,90,315,false);

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