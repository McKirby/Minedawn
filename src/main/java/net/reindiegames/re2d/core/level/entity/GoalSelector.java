package net.reindiegames.re2d.core.level.entity;

import java.util.Iterator;
import java.util.PriorityQueue;

import static net.reindiegames.re2d.core.CoreParameters.totalTicks;

public class GoalSelector {
    public final EntitySentient entity;

    protected PriorityQueue<EntityGoal> goals;
    private volatile EntityGoal active;

    protected GoalSelector(EntitySentient entity) {
        this.entity = entity;
        this.goals = new PriorityQueue<>();
    }

    public void add(EntityGoal goal) {
        synchronized (goals) {
            goals.add(goal);
        }
    }

    public void cancel() {
        synchronized (goals) {
            if (active != null) {
                active.yield(totalTicks);
            }
            this.active = null;
        }
    }

    public boolean select() {
        synchronized (goals) {
            if (active != null) {
                if (active.isDone(totalTicks)) {
                    active.yield(totalTicks);
                    active = null;
                } else {
                    active.iterate(totalTicks);
                }
                return true;
            }

            final Iterator<EntityGoal> goalIt = goals.iterator();
            EntityGoal goal;
            while (goalIt.hasNext()) {
                goal = goalIt.next();
                if (!goal.select(totalTicks)) continue;

                goal.execute(totalTicks);
                if (goal.isDone(totalTicks)) {
                    goal.yield(totalTicks);
                } else {
                    active = goal;
                }
                return true;
            }

            return false;
        }
    }

}
