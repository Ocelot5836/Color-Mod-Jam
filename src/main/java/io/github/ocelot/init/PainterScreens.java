package io.github.ocelot.init;

import io.github.ocelot.client.screen.BobRossTradeScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class PainterScreens
{
    public static void init()
    {
        ScreenManager.registerFactory(PainterContainers.BOB_ROSS.get(), BobRossTradeScreen::new);
    }
}
