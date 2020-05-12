package io.github.ocelot.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

/**
 * @author Ocelot
 */
public class WorldPainterTileEntityItemRenderer extends ItemStackTileEntityRenderer
{
    public static final WorldPainterTileEntityItemRenderer instance = new WorldPainterTileEntityItemRenderer();

    @Override
    public void render(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {

    }
}
