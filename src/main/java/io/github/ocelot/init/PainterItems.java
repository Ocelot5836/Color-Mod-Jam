package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.item.PaintbrushItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Ocelot
 */
public class PainterItems
{
    public static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, WorldPainter.MOD_ID);

    public static final RegistryObject<PaintbrushItem> PAINT_BRUSH = REGISTRY.register("paintbrush", () -> new PaintbrushItem(new Item.Properties().maxStackSize(1).group(WorldPainter.TAB)));
}
