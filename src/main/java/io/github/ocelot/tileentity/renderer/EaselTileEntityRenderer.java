package io.github.ocelot.tileentity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.block.EaselBlock;
import io.github.ocelot.painting.render.WorldPaintingRenderer;
import io.github.ocelot.tileentity.EaselTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

/**
 * @author Ocelot
 */
public class EaselTileEntityRenderer extends TileEntityRenderer<EaselTileEntity>
{
    public EaselTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(EaselTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        WorldPaintingRenderer.renderPainting(0, 0, 0, te.getBlockState().get(EaselBlock.HORIZONTAL_FACING), stack, buffer, te.getPainting(), combinedLight);
        // TODO render painting
    }
}
