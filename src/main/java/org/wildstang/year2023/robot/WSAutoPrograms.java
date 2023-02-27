package org.wildstang.year2023.robot;

import org.wildstang.framework.core.AutoPrograms;
import org.wildstang.year2023.auto.Programs.SampleAutoProgram;

/**
 * All active AutoPrograms are enumerated here.
 * It is used in Robot.java to initialize all programs.
 */
public enum WSAutoPrograms implements AutoPrograms {

    // enumerate programs
    SAMPLE_PROGRAM("Sample", SampleAutoProgram.class),
    // TEST_PROGRAM("Test Program", Testprogram.class),
    // BLUE_TOP_3PE("BLUE Top_3pE", Blue_Top_3pE.class),
    // RED_TOP_3PE("RED Top_3pE", Red_Top_3pE.class)
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