package io.github.willqi.pizzaserver.server.network.protocol.data.inventory.actions;

/**
 * Used for server authoritative inventories
 * Created when a player uses a recipe that generates byproducts.
 * (e.g. cake returns a bucket)
 */
public class InventoryActionCreate implements InventoryAction {

    private final int slot;


    public InventoryActionCreate(int slot) {
        this.slot = slot;
    }

    @Override
    public InventoryActionType getType() {
        return InventoryActionType.CREATE;
    }

    public int getSlot() {
        return this.slot;
    }

}
