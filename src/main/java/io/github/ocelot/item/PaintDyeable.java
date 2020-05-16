package io.github.ocelot.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Specifies that an item has the ability to be colored.</p>
 *
 * @author Ocelot
 */
public interface PaintDyeable
{
    /**
     * Checks whether or not the specified stack has any color.
     *
     * @param stack The stack to check
     * @return Whether or not there is color on the stack
     */
    default boolean hasColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", Constants.NBT.TAG_ANY_NUMERIC);
    }

    /**
     * Fetches the color from the specified stack.
     *
     * @param stack The stack to query
     * @return The color in that stack or <code>0xFFFFFF</code> if there is no color in the stack
     */
    default int getColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", Constants.NBT.TAG_ANY_NUMERIC) ? compoundnbt.getInt("color") : DyeColor.WHITE.getColorValue();
    }

    /**
     * Removes all color from the specified stack.
     *
     * @param stack The stack to remove color from
     */
    default void removeColor(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        if (compoundnbt != null && compoundnbt.contains("color"))
        {
            compoundnbt.remove("color");
        }
    }

    /**
     * Sets the color for the specified stack.
     *
     * @param stack The stack to set the color for
     * @param color The new color of the stack
     */
    default void setColor(ItemStack stack, int color)
    {
        stack.getOrCreateChildTag("display").putInt("color", color);
    }
}
