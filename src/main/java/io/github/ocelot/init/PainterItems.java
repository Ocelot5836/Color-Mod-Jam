package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.client.render.WorldPainterTileEntityItemRenderer;
import io.github.ocelot.item.AfroItem;
import io.github.ocelot.item.Paintbrush;
import io.github.ocelot.item.PaintbrushItem;
import io.github.ocelot.item.WorldPaintingItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
public class PainterItems
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, WorldPainter.MOD_ID);

    public static final RegistryObject<PaintbrushItem> SMALL_PAINT_BRUSH = ITEMS.register("small_paintbrush", () -> new PaintbrushItem(Paintbrush.BrushSize.SMALL, new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
    public static final RegistryObject<PaintbrushItem> MEDIUM_PAINT_BRUSH = ITEMS.register("medium_paintbrush", () -> new PaintbrushItem(Paintbrush.BrushSize.MEDIUM, new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
    public static final RegistryObject<PaintbrushItem> LARGE_PAINT_BRUSH = ITEMS.register("large_paintbrush", () -> new PaintbrushItem(Paintbrush.BrushSize.LARGE, new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
    public static final RegistryObject<WorldPaintingItem> WORLD_PAINTING = ITEMS.register("world_painting", () -> new WorldPaintingItem(false, new Item.Properties().setISTER(() -> WorldPainterTileEntityItemRenderer::new).group(WorldPainter.TAB)));
    public static final RegistryObject<WorldPaintingItem> TELEPORTATION_PAINTING = ITEMS.register("teleportation_painting", () -> new WorldPaintingItem(true, new Item.Properties().setISTER(() -> WorldPainterTileEntityItemRenderer::new).group(WorldPainter.TAB)));
    public static final RegistryObject<AfroItem> AFRO = ITEMS.register("afro", () -> new AfroItem(new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
}
