package io.github.ocelot.entity;

import io.github.ocelot.init.PainterItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Ocelot
 */
public class BobRossEntity extends MobEntity
{
    public BobRossEntity(EntityType<? extends MobEntity> type, World world)
    {
        super(type, world);
        this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(PainterItems.AFRO.get()));
    }
}
