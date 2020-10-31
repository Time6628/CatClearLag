package me.time6628.clag.sponge.api.events;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.api.Type;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

public class ClearLagEvent extends AbstractEvent {

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), CatClearLag.instance);
    }

    @Override
    public Object getSource() {
        return CatClearLag.instance;
    }

    @Override
    public EventContext getContext() {
        return EventContext.empty();
    }

    public static class Pre extends ClearLagEvent implements Cancellable {
        private boolean cancel;
        private final Type clearType;

        public Pre(Type clearType) {
            this.clearType = clearType;
        }

        @Override
        public boolean isCancelled() {
            return cancel;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }

        public Type getClearType() {
            return clearType;
        }
    }

    public static class Post extends ClearLagEvent {

        private final Type type;
        private final int amount;

        public Post(Type type, int i) {
            this.type = type;
            this.amount = i;
        }

        public Type getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }
    }
}
