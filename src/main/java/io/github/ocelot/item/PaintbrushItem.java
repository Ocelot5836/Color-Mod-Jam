package io.github.ocelot.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class PaintbrushItem extends Item
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
                setColor(stack, color.getColorValue());
                items.add(stack);
            }
        }
    }

    public static boolean hasColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", Constants.NBT.TAG_ANY_NUMERIC);
    }

    public static int getColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", Constants.NBT.TAG_ANY_NUMERIC) ? compoundnbt.getInt("color") : 0xFFFFFF;
    }

    public static void removeColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        if (compoundnbt != null && compoundnbt.contains("color"))
        {
            compoundnbt.remove("color");
        }
    }

    public static void setColor(ItemStack stack, int color)
    {
        stack.getOrCreateChildTag("display").putInt("color", color);
    }
}
