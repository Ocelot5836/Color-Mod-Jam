package io.github.ocelot.tileentity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.block.EaselBlock;
import io.github.ocelot.painting.render.WorldPaintingRenderer;
import io.github.ocelot.tileentity.EaselTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
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
        if (te.getPaintingId() != null)
        {
            stack.push();
            stack.translate(0.5, 0, 0.5);
            stack.rotate(Vector3f.YP.rotationDegrees(180 - te.getBlockState().get(EaselBlock.HORIZONTAL_FACING).getHorizontalAngle()));
            stack.scale(0.0625f, 0.0625f, 0.0625f);
            stack.translate(0, 24.25, 3.125);
            stack.rotate(Vector3f.XP.rotationDegrees(22.5f));
            stack.scale(0.5f, 0.5f, 0.5f);
            WorldPaintingRenderer.renderPainting(stack, buffer, te.getPainting(), false, false, combinedLight);
            stack.pop();
        }
    }
}
