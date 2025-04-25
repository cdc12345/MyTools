package org.cdc.toolbox;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.LootTable;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorFlavor.GamePlatform;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.toolbox.element.MyEnchantment;
import org.cdc.toolbox.ui.MyEnchantmentGUI;
import org.cdc.toolbox.ui.MyLoottableGUI;
import org.cdc.toolbox.utils.wrap.ModElementTypeLoaderWrap;

public class MyToolBoxMain extends JavaPlugin {

    private static final Logger LOG = LogManager.getLogger("Demo Java Plugin");

    public MyToolBoxMain(Plugin plugin) {
        super(plugin);

        addListener(PreGeneratorsLoadingEvent.class, a -> {
            try {
                //reInject
                var REGISTRY = new ModElementTypeLoaderWrap().getREGISTERIES();
                REGISTRY.remove(ModElementType.ENCHANTMENT);
                var type = new ModElementType<>("enchantment", 'm', MyEnchantmentGUI::new, MyEnchantment.class);
                ModElementType.ENCHANTMENT = type;
                ModElementTypeLoader.register(type).coveredOn(GeneratorFlavor.gamePlatform(GamePlatform.JAVAEDITION));

                REGISTRY.remove(ModElementType.LOOTTABLE);
                var type1 = new ModElementType<>("loottable", 'l', MyLoottableGUI::new, LootTable.class);
                ModElementType.LOOTTABLE = type1;
                ModElementTypeLoader.register(type1);
            } catch (Exception ignored) {
            }
        });
    }

}
