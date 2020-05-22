package io.github.ocelot.entity;

import io.github.ocelot.init.PainterItems;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

    public PlayerEntity getCustomer()
    {
        return customer;
    }

    public void setCustomer(@Nullable PlayerEntity customer)
    {
        this.customer = customer;
    }
}
