package org.cdc.blockstates.gui;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxType;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyAggregatedValidationResult;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.blockstates.types.BlockStates;
import org.cdc.blockstates.utils.BlocklyToVariant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockStatesGUI extends ModElementGUI<BlockStates> implements IBlocklyPanelHolder{

	private final ValidationGroup page1group = new ValidationGroup();

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private Map<String, ToolboxBlock> externalBlocks;
	private final List<IBlocklyPanelHolder.BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	public final static BlocklyEditorType BLOCK_STATES = new BlocklyEditorType("cvariants", null, "variant_trigger");

	private final VTextField blockRegistryName = new VTextField(20);

	public BlockStatesGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

	@Override public void addBlocklyChangedListener(IBlocklyPanelHolder.BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override protected void initGUI() {
		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BLOCK_STATES).getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator, BLOCK_STATES);

		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BLOCK_STATES)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.EMPTY);
			blocklyPanel.addChangeListener(
					changeEvent -> new Thread(BlockStatesGUI.this::regenerateTrigger, "TriggerRegenerate").start());
			if (!isEditingMode()) {
				blocklyPanel.setXML(
						"<xml><block type=\"variant_trigger\" deletable=\"false\" x=\"40\" y=\"80\"/></xml>");
			}
		});

		JPanel blockstateTrigger = (JPanel) PanelUtils.centerAndSouthElement(blocklyPanel, compileNotesPanel);
		blockstateTrigger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.cblockstates.trigger_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));


		addPage(blockstateTrigger, false).validate(page1group).lazyValidate(
				() -> new BlocklyAggregatedValidationResult(compileNotesPanel.getCompileNotes(),
						compileNote -> L10N.t("elementgui.advancement.trigger", compileNote)));
	}

	private synchronized void regenerateTrigger() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BLOCK_STATES));

		BlocklyToVariant blockvariant;
		try {
			blockvariant = new BlocklyToVariant(mcreator.getWorkspace(), this.modElement,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blockvariant.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel));
		});
	}

	@Override protected void openInEditingMode(BlockStates generatableElement) {
	}

	@Override public BlockStates getElementFromGUI() {
		BlockStates element = new BlockStates(modElement);
		element.filepath = modElement.getRegistryName().replace("_block_state","");
		return element;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI("google.com");
	}

	@Override public boolean isInitialXMLValid() {
		return false;
	}
}
