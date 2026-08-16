package fr.redsavant.bdapi.timeline;

/**
 * A timeline step. `execute()` is called when it is its turn
 * `onDone` must be called (immediately or after a delay/animation) to
 * signal to the executor to move on to the next step
 */
public interface TimelineStep {
    void execute(Runnable onDone);
}
