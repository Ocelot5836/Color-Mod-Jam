package io.github.ocelot.client.render.painting;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.ocelot.WorldPainter;
import io.github.ocelot.client.framebuffer.AdvancedFbo;
import io.github.ocelot.painting.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

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
    private static final ResourceLocation FBO_LOCATION = new ResourceLocation(WorldPainter.MOD_ID, "textures/entity/painting_fbo.png");

    private static final LazyValue<AdvancedFbo> IN_FRAMEBUFFER = new LazyValue<>(WorldPaintingRenderer::createInputFbo);
    private static Framebuffer outFbo;
    private static ShaderGroup shader;

    public static void init(IEventBus bus)
    {
        bus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event ->
        {
            Minecraft minecraft = Minecraft.getInstance();
            IResourceManager resourceManager = minecraft.getResourceManager();
            if (resourceManager instanceof IReloadableResourceManager)
            {
                ((IReloadableResourceManager) resourceManager).addReloadListener((stage, iResourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> CompletableFuture.allOf().thenCompose(stage::markCompleteAwaitingOthers).thenRunAsync(() -> CompletableFuture.runAsync(() -> loadShader(iResourceManager), gameExecutor)));
            }
        });
    }

    private static void loadShader(IResourceManager resourceManager)
    {
        if (shader != null)
            shader.close();
        try
        {
            shader = new ShaderGroup(Minecraft.getInstance().getTextureManager(), resourceManager, IN_FRAMEBUFFER.getValue().getVanillaWrapper(), SHADER_LOCATION);
            shader.createBindFramebuffers(IN_FRAMEBUFFER.getValue().getWidth(), IN_FRAMEBUFFER.getValue().getHeight());
            outFbo = shader.getFramebufferRaw("final");
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to load painting shader from '" + SHADER_LOCATION + "'.", e);
            shader = null;
            outFbo = null;
        }
    }

    private static AdvancedFbo createInputFbo()
    {
        AdvancedFbo fbo = new AdvancedFbo.Builder(16, 16).addColorTextureBuffer().build();
        fbo.create();
        return fbo;
    }

    private static void renderBackground(MatrixStack stack, IRenderTypeBuffer buffer, @Nullable Painting painting, boolean renderBorder, boolean renderEffects, TextureAtlasSprite backSprite, int combinedLight, float partialTicks)
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

                boolean useShader = shader == null || outFbo == null || !renderEffects;
                if (useShader)
                {
                    AdvancedFbo inFbo = IN_FRAMEBUFFER.getValue();
                    inFbo.bind(false);
                    {
                        inFbo.clear();
                    }
                }

                IVertexBuilder frontBuilder = buffer.getBuffer(RenderType.getEntitySolid(WorldPaintingTextureCache.getTexture(painting)));
                renderFront(matrix4f, matrix3f, frontBuilder, f15, f16, f17, f18, i, j, d0, d1, k, l, combinedLight);
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntitySolid(WorldPaintingTextureCache.getTexture(painting)));

                if (useShader)
                {
                    Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
                }

//                }
//                else
                {
//                    AdvancedFbo inFbo = IN_FRAMEBUFFER.getValue();
//                    inFbo.bind(true);
//                    {
//                        inFbo.clear();
//
//                        RenderSystem.matrixMode(GL_PROJECTION);
//                        RenderSystem.pushMatrix();
//                        RenderSystem.loadIdentity();
//                        RenderSystem.ortho(0, 1, 0, 1, 0.3, 1000.0);
//                        RenderSystem.matrixMode(GL_MODELVIEW);
//                        RenderSystem.pushMatrix();
//                        RenderSystem.loadIdentity();
//                        RenderSystem.translatef(0, 0, -10);
//
//                        Minecraft.getInstance().getTextureManager().bindTexture(WorldPaintingTextureCache.getTexture(painting));
//                        ShapeRenderer.drawRectWithTexture(0, 0, k, l, 1, 1, -1, -1, i, j);
//
//                        RenderSystem.matrixMode(GL_PROJECTION);
//                        RenderSystem.popMatrix();
//                        RenderSystem.matrixMode(GL_MODELVIEW);
//                        RenderSystem.popMatrix();
//                    }
//                    outFbo.bind(true);
//                    {
//                        outFbo.clear();
//
//                        RenderSystem.matrixMode(GL_PROJECTION);
//                        RenderSystem.pushMatrix();
//                        RenderSystem.loadIdentity();
//                        RenderSystem.ortho(0, inFbo.getWidth(), inFbo.getHeight(), 0, 0.3, 1000.0);
//                        RenderSystem.matrixMode(GL_MODELVIEW);
//                        RenderSystem.pushMatrix();
//                        RenderSystem.loadIdentity();
//                        RenderSystem.translatef(0, 0, -10);
//
//                        shader.render(partialTicks);
//
//                        RenderSystem.matrixMode(GL_PROJECTION);
//                        RenderSystem.popMatrix();
//                        RenderSystem.matrixMode(GL_MODELVIEW);
//                        RenderSystem.popMatrix();
//                    }
//
//                    Minecraft.getInstance().getFramebuffer().bindFramebuffer(true);
//
//                    IVertexBuilder fboBuilder = buffer.getBuffer(RenderType.getEntitySolid(FBO_LOCATION));
//                    pos(matrix4f, matrix3f, fboBuilder, f15, f18, 1, 0, -0.5F, 0, 0, -1, combinedLight);
//                    pos(matrix4f, matrix3f, fboBuilder, f16, f18, 0, 0, -0.5F, 0, 0, -1, combinedLight);
//                    pos(matrix4f, matrix3f, fboBuilder, f16, f17, 0, 1, -0.5F, 0, 0, -1, combinedLight);
//                    pos(matrix4f, matrix3f, fboBuilder, f15, f17, 1, 1, -0.5F, 0, 0, -1, combinedLight);
//                    Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntitySolid(FBO_LOCATION));
                }

                if (useShader)
                {
                    outFbo.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
                    Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
                }

                if (renderBorder && (painting == null || painting.hasBorder()))
                {
                    IVertexBuilder borderBuilder = buffer.getBuffer(RenderType.getEntityTranslucentCull(WorldPaintingTextureCache.BORDER));
                    renderFront(matrix4f, matrix3f, borderBuilder, f15, f16, f17, f18, i, j, d0, d1, k, l, combinedLight);
                    Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish(RenderType.getEntityTranslucentCull(WorldPaintingTextureCache.BORDER));
                }
            }
        }
    }

    private static void renderFront(@Nullable Matrix4f matrix4f, @Nullable Matrix3f matrix3f, IVertexBuilder builder, float f15, float f16, float f17, float f18, int i, int j, double d0, double d1, int k, int l, int combinedLight)
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

    private static void pos(@Nullable Matrix4f matrix, @Nullable Matrix3f normal, IVertexBuilder buffer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int combinedLight)
    {
        if (matrix != null)
        {
            buffer.pos(matrix, x, y, z);
        }
        else
        {
            buffer.pos(x, y, z);
        }

        buffer.color(255, 255, 255, 255).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLight);

        if (normal != null)
        {
            buffer.normal(normal, (float) normalX, (float) normalY, (float) normalZ);
        }
        else
        {
            buffer.normal((float) normalX, (float) normalY, (float) normalZ);
        }

        buffer.endVertex();
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
     * @param partialTicks  The percentage form last tick to this tick
     */
    public static void renderPainting(MatrixStack stack, IRenderTypeBuffer buffer, @Nullable Painting painting, boolean renderBorder, boolean renderEffects, int combinedLight, float partialTicks)
    {
        renderBackground(stack, buffer, painting, renderBorder, renderEffects, Minecraft.getInstance().getPaintingSpriteUploader().getBackSprite(), combinedLight, partialTicks);
    }
}
