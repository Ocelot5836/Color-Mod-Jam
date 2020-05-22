package io.github.ocelot.container;

import io.github.ocelot.entity.BobRossEntity;
import io.github.ocelot.init.PainterContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

/**
 * @author Ocelot
 */
public class BobRossContainer extends Container
{
    private final PlayerInventory playerInventory;
    private final BobRossEntity bobRoss;

    public BobRossContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, null);
    }

    public BobRossContainer(int id, PlayerInventory playerInventory, BobRossEntity bobRoss)
    {
        super(PainterContainers.BOB_ROSS.get(), id);
        this.playerInventory = playerInventory;
        this.bobRoss = bobRoss;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return this.bobRoss.isAlive() && this.bobRoss.getCustomer() == player && this.bobRoss.getPositionVec().squareDistanceTo(player.getPositionVec()) <= 64.0D;
    }
}
