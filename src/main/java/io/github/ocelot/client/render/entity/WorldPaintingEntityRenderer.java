package io.github.ocelot.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.client.render.painting.WorldPaintingRenderer;
import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
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
        WorldPaintingRenderer.renderPainting(stack, buffer, entity.getPainting(), true, entity.isTeleportation(), packedLight);
        stack.pop();
        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(WorldPaintingEntity entity)
    {
        return MissingTextureSprite.getLocation();
    }
}
