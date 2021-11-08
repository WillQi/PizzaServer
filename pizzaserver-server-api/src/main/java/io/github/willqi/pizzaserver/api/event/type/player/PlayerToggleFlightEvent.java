package io.github.willqi.pizzaserver.api.event.type.player;

import io.github.willqi.pizzaserver.api.player.Player;

/**
 * Sent when a player changes their flight status.
 */
public class PlayerToggleFlightEvent extends BasePlayerEvent.Cancellable {

    protected boolean flight;


    public PlayerToggleFlightEvent(Player player, boolean flight) {
        super(player);
        this.flight = flight;
    }

    public boolean isFlying() {
        return this.flight;
    }

    public void setFlying(boolean enabled) {
        this.flight = enabled;
    }

}
