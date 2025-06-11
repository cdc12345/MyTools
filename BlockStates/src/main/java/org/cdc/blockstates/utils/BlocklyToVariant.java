package org.cdc.blockstates.utils;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.blocks.TextBlock;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.blockstates.gui.BlockStatesGUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

public class BlocklyToVariant extends BlocklyToCode {
	private static final Logger LOG = LogManager.getLogger("Blockly2Variant");

	public BlocklyToVariant(Workspace workspace, ModElement parent, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, parent, BlockStatesGUI.BLOCK_STATES, templateGenerator, externalGenerators);

		blockGenerators.add(new TextBlock());

		if (sourceXML != null) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, editorType.startBlockName());

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				processBlockProcedure(base_blocks);
			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error("Failed to parse Blockly XML", e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.exception_compiling", e.getMessage())));
			}
		} else {
			addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.editor_not_ready")));
		}
	}
}
