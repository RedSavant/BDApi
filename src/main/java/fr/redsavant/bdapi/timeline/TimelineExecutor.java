package fr.redsavant.bdapi.timeline;

import java.util.List;

/** Executes timeline steps sequentially, optionally restarting from the first step. */
final class TimelineExecutor {
    private final List<TimelineStep> steps;
    private final boolean loop;
    private int index;
    private boolean cancelled;

    TimelineExecutor(List<TimelineStep> steps, boolean loop) {
        this.steps = steps;
        this.loop = loop;
    }

    void start() {
        index = 0;
        cancelled = false;
        runNext();
    }

    void cancel() {
        cancelled = true;
    }

    private void runNext() {
        if (cancelled || steps.isEmpty()) return;
        if (index >= steps.size()) {
            if (!loop) return;
            index = 0;
        }
        TimelineStep step = steps.get(index++);
        step.execute(this::runNext);
    }
}
