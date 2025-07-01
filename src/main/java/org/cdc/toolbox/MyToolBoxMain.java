package org.cdc.toolbox;

import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyToolBoxMain extends JavaPlugin {

    private static final Logger LOG = LogManager.getLogger("Demo Java Plugin");

    public MyToolBoxMain(Plugin plugin) {
        super(plugin);

/*        addListener(PreGeneratorsLoadingEvent.class, a -> {
            try {
                //reInject
                var REGISTRY = new ModElementTypeLoaderWrap().getREGISTERIES();
                REGISTRY.remove(ModElementType.ENCHANTMENT);
                var type = new ModElementType<>("enchantment", 'm', MyEnchantmentGUI::new, MyEnchantment.class);
                ModElementType.ENCHANTMENT = type;
                ModElementTypeLoader.register(type).coveredOn(GeneratorFlavor.gamePlatform(GamePlatform.JAVAEDITION));

                REGISTRY.remove(ModElementType.RECIPE);
                ModElementType.RECIPE = ModElementTypeLoader.register(new ModElementType<>("recipe", 'r', ERecipeGUI::new, Recipe.class));
            } catch (Exception ignored) {
            }
        });*/

        addListener(ModElementGUIEvent.WhenSaving.class,a->{
            if (!a.getMCreator().getActionRegistry().buildWorkspace.isEnabled()) {
                a.getMCreator().getGradleConsole().exec("build");
                a.getMCreator().getGradleConsole().markRunning();
            }
        });
    }

}
