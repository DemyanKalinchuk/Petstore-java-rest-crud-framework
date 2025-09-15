package core;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import static lombok.AccessLevel.PRIVATE;

@Log4j
@NoArgsConstructor(access = PRIVATE)

public class TestStepLogger {
    private static int preConditionStepNumber = 1;
    private static int postConditionStepNumber = 1;
    private static int logStep = 1;

    public static final String RESET = "\033[0m";  // Text Reset

    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE

    public static void logStep(String message) {
        log.info(BLUE_BOLD + "STEP " + logStep++ + " : " + message + RESET);
    }

    public static void logPreConditionStep(String message) {
        log.info(GREEN_BOLD + "Pre-condition STEP " + preConditionStepNumber++ + " : " + message + RESET);
    }

    public static void logPostConditionStep(String message) {
        log.info(YELLOW_BOLD + "Post-condition STEP " + postConditionStepNumber++ + " : " + message + RESET);
    }

    public static void log(String message) {
        log.info("Additional test information: " + message + "\n");
    }

    public static void resetCounters() {
        preConditionStepNumber = 1;
        postConditionStepNumber = 1;
        logStep = 1;

        log.info(RESET);
    }
}
