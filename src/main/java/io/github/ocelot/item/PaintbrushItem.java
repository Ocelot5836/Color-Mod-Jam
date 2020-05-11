package io.github.ocelot.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

/**
 * @author Ocelot
 */
public class PaintbrushItem extends Item implements PaintDyeable
{
    public PaintbrushItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            for (DyeColor color : DyeColor.values())
            {
                ItemStack stack = new ItemStack(this);
                this.setColor(stack, color.getColorValue());
                this.setPaint(stack, MAX_PAINT);
                items.add(stack);
            }
        }
    }
}
