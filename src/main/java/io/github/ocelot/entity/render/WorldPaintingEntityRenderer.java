package io.github.ocelot.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author Ocelot
 */
public class WorldPaintingEntityRenderer extends EntityRenderer<WorldPaintingEntity>
{
    public WorldPaintingEntityRenderer(EntityRendererManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void render(WorldPaintingEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn)
    {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(WorldPaintingEntity entity)
    {
        return null;
    }
}
