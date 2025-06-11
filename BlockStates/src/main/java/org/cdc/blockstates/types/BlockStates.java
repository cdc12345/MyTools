package org.cdc.blockstates.types;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.blockstates.gui.BlockStatesGUI;
import org.cdc.blockstates.utils.BlocklyToVariant;

import javax.annotation.Nullable;

public class BlockStates extends GeneratableElement {

	public String filepath;

	@BlocklyXML("cvariants") public String triggerxml;

	public BlockStates(ModElement element) {
		super(element);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlockStatesGUI.BLOCK_STATES).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.JSON_TRIGGER),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlockStatesGUI.BLOCK_STATES.registryName()),
					additionalData).setTemplateExtension("json");

			// load blocklytojsontrigger with custom generators loaded
			BlocklyToVariant blocklyToVariant = new BlocklyToVariant(this.getModElement().getWorkspace(),
					this.getModElement(), this.triggerxml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlockStatesGUI.BLOCK_STATES.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			String triggerCode = blocklyToVariant.getGeneratedCode();

			additionalData.put("triggercode", triggerCode);
			additionalData.put("triggerblocks", blocklyToVariant.getUsedBlocks());
			additionalData.put("extra_templates_code", blocklyToVariant.getExtraTemplatesCode());
		};
	}

	private String getOriginalCode(){
		return "";
	}
}
