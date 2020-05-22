package io.github.ocelot.client.render.painting;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.ocelot.client.framebuffer.AdvancedFbo;
import io.github.ocelot.painting.Painting;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * <p>Manages the rendering of {@link Painting} onto a painting frame.</p>
 *
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class WorldPaintingRenderer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation SHADER_LOCATION = new ResourceLocation("shaders/post/wobble.json");

    private static AdvancedFbo fbo;
    private static ShaderGroup rippleShader;
    private static boolean useShader;
    private static boolean drawing;

    private static void createShader()
    {
        Minecraft minecraft = Minecraft.getInstance();
        try
        {
            rippleShader = new ShaderGroup(minecraft.getTextureManager(), minecraft.getResourceManager(), fbo.getVanillaWrapper(), SHADER_LOCATION);
            rippleShader.createBindFramebuffers(minecraft.getMainWindow().getFramebufferWidth(), minecraft.getMainWindow().getFramebufferHeight());
            useShader = true;
        }
        catch (IOException | JsonSyntaxException e)
        {
            LOGGER.warn("Failed to load shader: {}", SHADER_LOCATION, e);
            useShader = false;
        }
    }

    private static void updateFbo()
    {
        MainWindow window = Minecraft.getInstance().getMainWindow();
        if (fbo == null)
            fbo = createFbo(window.getFramebufferWidth(), window.getFramebufferHeight());

        if (rippleShader == null)
            createShader();

        if (fbo.getWidth() != window.getFramebufferWidth() || fbo.getHeight() != window.getFramebufferHeight())
        {
            fbo.close();
            fbo = createFbo(window.getFramebufferWidth(), window.getFramebufferHeight());
            rippleShader.createBindFramebuffers(window.getFramebufferWidth(), window.getFramebufferHeight());
        }
    }

    private static AdvancedFbo createFbo(int width, int height)
    {
        AdvancedFbo fbo = new AdvancedFbo.Builder(width, height).addColorTextureBuffer().setDepthRenderBuffer().build();
        fbo.create();
        return fbo;
    }

    private static void renderBackground(MatrixStack stack, IRenderTypeBuffer buffer, @Nullable Painting painting, boolean renderBorder, boolean renderEffects, TextureAtlasSprite backSprite, int combinedLight)
    {
        MatrixStack.Entry last = stack.getLast();
        Matrix4f matrix4f = last.getMatrix();
        Matrix3f matrix3f = last.getNormal();
        float f = (float) (-Painting.SIZE) / 2.0F;
        float f1 = (float) (-Painting.SIZE) / 2.0F;
        float f3 = backSprite.getMinU();
        float f4 = backSprite.getMaxU();
        float f5 = backSprite.getMinV();
        float f6 = backSprite.getMaxV();
        float f7 = backSprite.getMinU();
        float f8 = backSprite.getMaxU();
        float f9 = backSprite.getMinV();
        float f10 = backSprite.getInterpolatedV(1.0D);
        float f11 = backSprite.getMinU();
        float f12 = backSprite.getInterpolatedU(1.0D);
        float f13 = backSprite.getMinV();
        float f14 = backSprite.getMaxV();
        int i = Painting.SIZE / 16;
        int j = Painting.SIZE / 16;
        double d0 = 16.0D / (double) i;
        double d1 = 16.0D / (double) j;

        for (int k = 0; k < i; ++k)
        {
            for (int l = 0; l < j; ++l)
            {
                float f15 = f + (float) ((k + 1) * 16);
                float f16 = f + (float) (k * 16);
                float f17 = f1 + (float) ((l + 1) * 16);
                float f18 = f1 + (float) (l * 16);

                IVertexBuilder builder = buffer.getBuffer(RenderType.getEntitySolid(backSprite.getAtlasTexture().getTextureLocation()));
                pos(matrix4f, matrix3f, builder, f15, f17, f3, f5, 0.5F, 0, 0, 1, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f17, f4, f5, 0.5F, 0, 0, 1, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f18, f4, f6, 0.5F, 0, 0, 1, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f18, f3, f6, 0.5F, 0, 0, 1, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f17, f7, f9, -0.5F, 0, 1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f17, f8, f9, -0.5F, 0, 1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f17, f8, f10, 0.5F, 0, 1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f17, f7, f10, 0.5F, 0, 1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f18, f7, f9, 0.5F, 0, -1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f18, f8, f9, 0.5F, 0, -1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f18, f8, f10, -0.5F, 0, -1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f18, f7, f10, -0.5F, 0, -1, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f17, f12, f13, 0.5F, -1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f18, f12, f14, 0.5F, -1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f18, f11, f14, -0.5F, -1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f15, f17, f11, f13, -0.5F, -1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f17, f12, f13, -0.5F, 1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f18, f12, f14, -0.5F, 1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f18, f11, f14, 0.5F, 1, 0, 0, combinedLight);
                pos(matrix4f, matrix3f, builder, f16, f17, f11, f13, 0.5F, 1, 0, 0, combinedLight);
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntitySolid(backSprite.getAtlasTexture().getTextureLocation()));

                IVertexBuilder frontBuilder = buffer.getBuffer(RenderType.getEntitySolid(WorldPaintingTextureCache.getTexture(painting)));
                renderFront(matrix4f, matrix3f, frontBuilder, f15, f16, f17, f18, i, j, d0, d1, k, l, combinedLight);
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntitySolid(WorldPaintingTextureCache.getTexture(painting)));

                if (renderBorder && (painting == null || painting.hasBorder()))
                {
                    IVertexBuilder borderBuilder = buffer.getBuffer(RenderType.getEntityTranslucentCull(WorldPaintingTextureCache.BORDER));
                    renderFront(matrix4f, matrix3f, borderBuilder, f15, f16, f17, f18, i, j, d0, d1, k, l, combinedLight);
                    Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntityTranslucentCull(WorldPaintingTextureCache.BORDER));
                }
            }
        }
    }

    private static void renderFront(Matrix4f matrix4f, Matrix3f matrix3f, IVertexBuilder builder, float f15, float f16, float f17, float f18, int i, int j, double d0, double d1, int k, int l, int combinedLight)
    {
        float f19 = (float) (0.0625 * (d0 * (i - k)));
        float f20 = (float) (0.0625 * (d0 * (double) (i - (k + 1))));
        float f21 = (float) (0.0625 * (d1 * (double) (j - l)));
        float f22 = (float) (0.0625 * (d1 * (double) (j - (l + 1))));
        pos(matrix4f, matrix3f, builder, f15, f18, f20, f21, -0.5F, 0, 0, -1, combinedLight);
        pos(matrix4f, matrix3f, builder, f16, f18, f19, f21, -0.5F, 0, 0, -1, combinedLight);
        pos(matrix4f, matrix3f, builder, f16, f17, f19, f22, -0.5F, 0, 0, -1, combinedLight);
        pos(matrix4f, matrix3f, builder, f15, f17, f20, f22, -0.5F, 0, 0, -1, combinedLight);
    }

    private static void pos(Matrix4f matrix, Matrix3f normal, IVertexBuilder buffer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int combinedLight)
    {
        buffer.pos(matrix, x, y, z).color(255, 255, 255, 255).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLight).normal(normal, (float) normalX, (float) normalY, (float) normalZ).endVertex();
    }

    /**
     * Renders the specified world painting.
     *
     * @param stack         The matrix stack
     * @param buffer        The render buffers
     * @param painting      The painting to draw or null for a blank image
     * @param renderBorder  Whether or not to draw a border on the painting
     * @param renderEffects Whether or not to draw to the shader buffer
     * @param combinedLight The combined light of the painting
     */
    public static void renderPainting(MatrixStack stack, IRenderTypeBuffer buffer, @Nullable Painting painting, boolean renderBorder, boolean renderEffects, int combinedLight)
    {
        PaintingSpriteUploader paintingspriteuploader = Minecraft.getInstance().getPaintingSpriteUploader();
        TextureAtlasSprite backSprite = paintingspriteuploader.getBackSprite();
        updateFbo();
        renderBackground(stack, buffer, painting, renderBorder, renderEffects, backSprite, combinedLight);
    }
}
