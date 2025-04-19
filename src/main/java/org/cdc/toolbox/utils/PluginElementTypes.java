package org.cdc.toolbox.utils;

import net.mcreator.element.ModElementType;
import org.cdc.toolbox.element.Blockstates;
import org.cdc.toolbox.ui.BlockstatesGUI;

import static net.mcreator.element.ModElementTypeLoader.register;

public class PluginElementTypes {
    public static ModElementType<?> BLOCKSTATES;

    public static void load() {

        BLOCKSTATES = register(
                new ModElementType<>("blockstates",null, BlockstatesGUI::new, Blockstates.class)
        );

    }

}
