package org.wildstang.year2023.robot;

import org.wildstang.framework.core.AutoPrograms;
import org.wildstang.year2023.auto.Programs.SampleAutoProgram;
import org.wildstang.year2023.auto.Programs.Testprogram;
import org.wildstang.year2023.auto.Programs.AutobalanceTest;
import org.wildstang.year2023.auto.Programs.Blue_Bot_3pE;
import org.wildstang.year2023.auto.Programs.Blue_Middle;
import org.wildstang.year2023.auto.Programs.Blue_Top_3;
import org.wildstang.year2023.auto.Programs.Blue_Top_3pE;
import org.wildstang.year2023.auto.Programs.Red_Bot_3pE;
import org.wildstang.year2023.auto.Programs.Red_Middle;
import org.wildstang.year2023.auto.Programs.Red_Top_3;
import org.wildstang.year2023.auto.Programs.Red_Top_3pE;

/**
 * All active AutoPrograms are enumerated here.
 * It is used in Robot.java to initialize all programs.
 */
public enum WSAutoPrograms implements AutoPrograms {

    // enumerate programs
    //SAMPLE_PROGRAM("Sample", SampleAutoProgram.class),
    //TEST_PROGRAM("Test Program", Testprogram.class),
    //AUTOBALANCE("autobalance", AutobalanceTest.class),
    BLUE_TOP_3PE("BLUE Top_3pE", Blue_Top_3pE.class),
    BLUE_TOP_3("BLUE Top_3", Blue_Top_3.class),
    BLUE_BOT_3PE("BLUE Bot_3pE", Blue_Bot_3pE.class),
    BLUE_MIDDLE("BLUE Middle", Blue_Middle.class),
    RED_TOP_3PE("RED Top_3pE", Red_Top_3pE.class),
    RED_TOP_3("RED Top_3", Red_Top_3.class),
    RED_BOT_3PE("RED Bot_3pE", Red_Bot_3pE.class),
    RED_MIDDLE("RED Middle", Red_Middle.class)

    ;

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */
    
    private String name;
    private Class<?> programClass;

    /**
     * Initialize name and AutoProgram map.
     * @param name Name, must match that in class to prevent errors.
     * @param programClass Class containing AutoProgram
     */
    WSAutoPrograms(String name, Class<?> programClass) {
        this.name = name;
        this.programClass = programClass;
    }

    /**
     * Returns the name mapped to the AutoProgram.
     * @return Name mapped to the AutoProgram.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns AutoProgram's class.
     * @return AutoProgram's class.
     */
    @Override
    public Class<?> getProgramClass() {
        return programClass;
    }
}