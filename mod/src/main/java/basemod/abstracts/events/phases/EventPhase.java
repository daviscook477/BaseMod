package basemod.abstracts.events.phases;

import basemod.abstracts.events.PhasedEvent;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

public abstract class EventPhase {
    private Consumer<EventPhase> updateHandler = null;

    public abstract void transition(PhasedEvent event);
    public abstract void hide(PhasedEvent event);

    public void reset() {
        updateHandler = null;
    }

    public void setUpdateHandler(Consumer<EventPhase> handler) {
        updateHandler = handler;
    }

    public Consumer<EventPhase> getUpdateHandler() {
        return updateHandler;
    }

    public void update() {
        if (updateHandler != null) updateHandler.accept(this);
    }

    public void render(SpriteBatch sb) {

    }
    public void renderAboveTopPanel(SpriteBatch sb) {

    }

    public boolean reopen(PhasedEvent phasedEvent) {
        return false;
    }
}
