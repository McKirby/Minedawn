package net.reindiegames.re2d.core.level.entity;

import java.util.ArrayList;
import java.util.List;

public class TargetSelector {
    public final EntitySentient entity;
    private final List<TargetReceiver<? extends Entity>> receivers;

    protected TargetSelector(EntitySentient entity) {
        this.entity = entity;
        this.receivers = new ArrayList<>();
    }

    public <E extends Entity> void add(TargetReceiver<E> receiver) {
        receivers.add(receiver);
    }

    public boolean validTarget(Entity target, float distance) {
        return entity.isHostileTowards(target) && target.isAlive() && entity.hasLineOfSight(target, distance, false);
    }

    public void select() {
        receivers.forEach(receiver -> {
            final Entity target = receiver.getTarget();

            if (target != null) {
                if (!TargetSelector.this.validTarget(target, receiver.getTargetDistance())) {
                    receiver.loseTarget();
                }
            } else {
                entity.visibleEntities(receiver.getTargetClass(), receiver.getTargetDistance()).filter(e -> {
                    return receiver.offerTarget(e);
                }).findFirst().ifPresent(newTarget -> {
                    receiver.setTarget(newTarget);
                });
            }
        });
    }
}
