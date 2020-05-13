package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.entity.render.WorldPainterTileEntityItemRenderer;
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

    public static final RegistryObject<PaintbrushItem> PAINT_BRUSH = ITEMS.register("paintbrush", () -> new PaintbrushItem(new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
    public static final RegistryObject<WorldPaintingItem> WORLD_PAINTING = ITEMS.register("world_painting", () -> new WorldPaintingItem(new Item.Properties().setISTER(() -> () -> WorldPainterTileEntityItemRenderer.instance).group(WorldPainter.TAB)));
}
