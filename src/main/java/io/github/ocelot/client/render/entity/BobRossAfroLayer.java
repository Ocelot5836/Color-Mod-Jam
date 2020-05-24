package io.github.ocelot.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.client.render.AfroRenderer;
import io.github.ocelot.entity.BobRossEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class BobRossAfroLayer extends LayerRenderer<BobRossEntity, PlayerModel<BobRossEntity>>
{
    public BobRossAfroLayer(BobRossEntityRenderer renderer)
    {
        super(renderer);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, BobRossEntity bobRoss, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!bobRoss.isSheared())
        {
            AfroRenderer.render(bobRoss, matrixStack, buffer, packedLight);
        }
    }
}
