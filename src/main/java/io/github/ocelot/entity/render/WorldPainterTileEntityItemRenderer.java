package io.github.ocelot.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.item.WorldPaintingItem;
import io.github.ocelot.painting.Painting;
import io.github.ocelot.painting.PaintingManager;
import io.github.ocelot.painting.render.WorldPaintingRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Ocelot
 */
public class WorldPainterTileEntityItemRenderer extends ItemStackTileEntityRenderer
{
    @Override
    public void render(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        World world = Minecraft.getInstance().world;
        if (world == null)
            return;
        if (stack.getItem() instanceof WorldPaintingItem)
        {
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0);
            matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
            Painting painting = PaintingManager.get(world).getPainting(((WorldPaintingItem) stack.getItem()).getPaintingId(stack));
            WorldPaintingRenderer.renderPainting(matrixStack, buffer, painting, true, false, combinedLight);
            matrixStack.pop();
        }
    }
}
