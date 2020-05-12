package io.github.ocelot.tileentity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
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
    public void render(EaselTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        // TODO render painting
    }
}
