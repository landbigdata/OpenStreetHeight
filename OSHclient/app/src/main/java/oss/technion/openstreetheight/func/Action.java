package oss.technion.openstreetheight.func;

/**
 * A functional interface similar to Runnable.
 */
public interface Action {
    /**
     * Runs the action and optionally throws a checked exception.
     */
    void run();
}