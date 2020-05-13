package io.github.ocelot.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.painting.render.WorldPaintingRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
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
    public void render(WorldPaintingEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight)
    {
        stack.push();
        stack.rotate(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
        stack.scale(0.0625F, 0.0625F, 0.0625F);
        WorldPaintingRenderer.renderPainting(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.getHorizontalFacing(), stack, buffer, entity.getPainting(), packedLight);
        stack.pop();
        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(WorldPaintingEntity entity)
    {
        return MissingTextureSprite.getLocation();
    }
}
