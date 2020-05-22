package io.github.ocelot.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.WorldPainter;
import io.github.ocelot.entity.BobRossEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class BobRossEntityRenderer extends MobRenderer<BobRossEntity, PlayerModel<BobRossEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(WorldPainter.MOD_ID, "textures/entity/bob_ross.png");

    public BobRossEntityRenderer(EntityRendererManager renderManager)
    {
        super(renderManager, new PlayerModel<>(0.0f, false), 0.5f);
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ArrowLayer<>(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new SpinAttackEffectLayer<>(this));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(BobRossEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(BobRossEntity entity, MatrixStack matrixStack, float partialTickTime) {
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected void renderName(BobRossEntity entity, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        double d0 = this.renderManager.squareDistanceTo(entity);
        matrixStack.push();
        if (d0 < 100.0D) {
            Scoreboard scoreboard = entity.getEntityWorld().getScoreboard();
            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
            if (scoreobjective != null) {
                Score score = scoreboard.getOrCreateScore(entity.getScoreboardName(), scoreobjective);
                super.renderName(entity, score.getScorePoints() + " " + scoreobjective.getDisplayName().getFormattedText(), matrixStack, buffer, packedLight);
                matrixStack.translate(0.0D, 9.0F * 1.15F * 0.025F, 0.0D);
            }
        }

        super.renderName(entity, displayName, matrixStack, buffer, packedLight);
        matrixStack.pop();
    }

    @Override
    protected void applyRotations(BobRossEntity entity, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        float f = entity.getSwimAnimation(partialTicks);
        if (entity.isElytraFlying())
        {
            super.applyRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
            float f1 = (float) entity.getTicksElytraFlying() + partialTicks;
            float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entity.isSpinAttacking())
            {
                matrixStack.rotate(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entity.rotationPitch)));
            }

            Vec3d vec3d = entity.getLook(partialTicks);
            Vec3d vec3d1 = entity.getMotion();
            double d0 = Entity.horizontalMag(vec3d1);
            double d1 = Entity.horizontalMag(vec3d);
            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                matrixStack.rotate(Vector3f.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
            }
        }
        else if (f > 0.0F)
        {
            super.applyRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
            float f3 = entity.isInWater() ? -90.0F - entity.rotationPitch : -90.0F;
            float f4 = MathHelper.lerp(f, 0.0F, f3);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(f4));
            if (entity.isActualySwimming())
            {
                matrixStack.translate(0.0D, -1.0D, 0.3F);
            }
        }
        else
        {
            super.applyRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }
    }
}
