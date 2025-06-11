package org.cdc.blockstates;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import org.cdc.blockstates.gui.BlockStatesGUI;
import org.cdc.blockstates.types.BlockStates;

public class BlockStateMain extends JavaPlugin {

	public BlockStateMain(Plugin plugin) {
		super(plugin);

		addListener(PreGeneratorsLoadingEvent.class,a->{
			ModElementTypeLoader.register(new ModElementType<>("cblockstates", null, BlockStatesGUI::new, BlockStates.class));
			BlocklyLoader.INSTANCE.addBlockLoader(BlockStatesGUI.BLOCK_STATES);
		});
	}
}
