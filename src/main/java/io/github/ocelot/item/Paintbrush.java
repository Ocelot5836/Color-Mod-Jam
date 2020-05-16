package io.github.ocelot.item;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.block.EaselBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.Locale;

/**
 * <p>An item that can be used to draw onto an {@link EaselBlock}</p>
 *
 * @author Ocelot
 */
public interface Paintbrush extends PaintDyeable
{
    /**
     * Fetches the brush from the specified stack.
     *
     * @param stack The stack to get the brush from
     * @return The size of this paint brush
     */
    BrushSize getBrush(ItemStack stack);

    /**
     * Fetches the amount of paint from the specified stack.
     *
     * @param stack The stack to query
     * @return The amount of paint left in the stack
     */
    default int getPaint(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null && compoundnbt.contains("paint", Constants.NBT.TAG_ANY_NUMERIC) ? compoundnbt.getInt("paint") : 0;
    }

    /**
     * Sets the amount of paint for the specified stack.
     *
     * @param stack The stack to set the paint amount for
     * @param paint The new amount of paint for the stack
     */
    default void setPaint(ItemStack stack, int paint)
    {
        stack.getOrCreateTag().putInt("paint", paint);
    }

    /**
     * <p>The sizes of brush that can be used to paint with.</p>
     *
     * @author Ocelot
     */
    enum BrushSize
    {
        SMALL(64, 1, 1), MEDIUM(32, 3, 3), LARGE(16, 5, 5);

        private final int maxPaint;
        private final int width;
        private final int height;

        BrushSize(int maxPaint, int width, int height)
        {
            this.maxPaint = maxPaint;
            this.width = width;
            this.height = height;
        }

        /**
         * @return The translation key of the display name of this brush type
         */
        public String getTranslationKey()
        {
            return "item." + WorldPainter.MOD_ID + ".paintbrush.brush." + this.name().toLowerCase(Locale.ROOT);
        }

        /**
         * @return The maximum amount of paint that can be used before needing a refill
         */
        public int getMaxPaint()
        {
            return maxPaint;
        }

        /**
         * @return The width of the paint area
         */
        public int getWidth()
        {
            return width;
        }

        /**
         * @return The height of the paint area
         */
        public int getHeight()
        {
            return height;
        }
    }
}
