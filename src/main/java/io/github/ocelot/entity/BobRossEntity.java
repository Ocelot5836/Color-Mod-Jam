package io.github.ocelot.entity;

import io.github.ocelot.container.BobRossContainer;
import io.github.ocelot.init.PainterItems;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class BobRossEntity extends CreatureEntity implements INPC
{
    private PlayerEntity customer;

    public BobRossEntity(EntityType<? extends CreatureEntity> type, World world)
    {
        super(type, world);
        this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(PainterItems.AFRO.get()));
    }

    private void displayMerchantGui(ServerPlayerEntity player)
    {
        this.setCustomer(player);
        NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new BobRossContainer(id, playerInventory, this), this.getDisplayName()));
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand)
    {
        if (!this.world.isRemote())
            this.displayMerchantGui((ServerPlayerEntity) player);
        return true;
    }

    public PlayerEntity getCustomer()
    {
        return customer;
    }

    public void setCustomer(@Nullable PlayerEntity customer)
    {
        this.customer = customer;
    }
}
