package com.mach.core.config;

import com.mach.core.model.SuiteResult;

public enum MachExecutionStatus {

    SCHEDULED("[\uD83D\uDD52](# \"Programada\")", "#F3DF42"),
    RUNNING("[\uD83C\uDFC3](# \"Ejecutando\")", "#F4DF42"),
    PASSED("[✔](# \"Éxito\")️", "#00D000"),
    FAILED("[❌](# \"Error\")", "#D00000"),
    SKIPPED("[❗️](# \"No ejecutado\")", "#F5DF42");

    private String message;
    private String color;

    MachExecutionStatus(String message, String color) {
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public String getColor() {
        return color;
    }

    public static MachExecutionStatus fromSuiteResult(SuiteResult suiteResult) {
        if (suiteResult.getFailedResults() > 0 || suiteResult.getPassedResults() == 0 || suiteResult.getFailedConfigs() > 0) {
            return FAILED;
        } else if (suiteResult.getFailedResults() == 0 && suiteResult.getPassedResults() == 0 && suiteResult.getSkippedResults() > 0) {
            return SKIPPED;
        } else {
            return PASSED;
        }
    }

}
