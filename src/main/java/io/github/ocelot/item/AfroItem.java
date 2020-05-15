package io.github.ocelot.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class AfroItem extends Item
{
    public AfroItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack)
    {
        return ModList.get().isLoaded("curios") ? null : EquipmentSlotType.HEAD;
    }
}
