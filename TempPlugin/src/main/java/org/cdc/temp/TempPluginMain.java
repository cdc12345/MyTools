package org.cdc.temp;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.LootTable;
import net.mcreator.java.JavaConventions;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.DataListBuilder;
import org.cdc.temp.ui.ELoottableGUI;
import org.cdc.temp.ui.TempItemGUI;
import org.cdc.temp.ui.TempLoottableTypeGUI;
import org.cdc.temp.utils.wrap.ModElementTypeLoaderWrap;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TempPluginMain extends JavaPlugin {
	public static final Logger LOG = LogManager.getLogger(TempPluginMain.class);

	public TempPluginMain(Plugin plugin) {
		super(plugin);

		String uuid = String.valueOf(System.currentTimeMillis());
		File pluginFolder = new File(System.getProperty("user.dir"), "plugins/tempplugin" + uuid);
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(pluginFolder);

		this.addListener(PreGeneratorsLoadingEvent.class, a -> {
			LOG.info("Modifying the ModElement");
			ModElementTypeLoaderWrap wrap = new ModElementTypeLoaderWrap();
			try {
				var reg = wrap.getREGISTERIES();
				reg.remove(ModElementType.LOOTTABLE);

				ModElementType.LOOTTABLE = ModElementTypeLoader.register(
						new ModElementType<>("loottable", 'l', ELoottableGUI::new, LootTable.class));
			} catch (Exception e) {
				LOG.info(e);
			}
		});

		this.addListener(MCreatorLoadedEvent.class, a -> {
			var mcreator = a.getMCreator();
			if (!(mcreator instanceof ModMaker)) {
				return;
			}

			var mainMenuBar = mcreator.getMainMenuBar();

			JMenu temp = new JMenu("TempPlugin");
			mainMenuBar.add(temp);

			JMenuItem addTempItem = new JMenuItem(L10N.t("menubar.item.addtempitem"));
			final DataListBuilder[] dataListBuilder = { null };
			addTempItem.addActionListener(b -> {
				var gui = new TempItemGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(generatableElement -> {
					String readableName = generatableElement.readableName;
					String type = generatableElement.type;
					String code = generatableElement.code;
					String registryName = generatableElement.registryName;
					if (readableName == null || type == null || registryName == null) {
						return;
					}

					if (dataListBuilder[0] == null) {
						dataListBuilder[0] = mcr.createDataList().setName("blocksitems");
					}
					if (code.isEmpty()) {
						if ("item".equals(type)) {
							code = "Items.AIR";
						} else if ("block".equals(type)) {
							code = "Blocks.AIR";
						} else {
							code = "Null";
						}
					}
					var arr = registryName.split(":");
					code = String.format(code, arr[0], arr[1]);
					dataListBuilder[0].appendElement(String.format("""
									%s: 
									  readable_name: "%s"
									  type: %s
									  """, StringUtils.uppercaseFirstLetter(type) + "s" + "."
									+ JavaConventions.convertToValidClassName(readableName), readableName, type),
							String.format("""
									
									  - %s
									  - "%s"
									""", code, registryName)).initGenerator().buildAndOutput();
					mcreator.getTabs().closeTab(tab);
				});
				mcreator.getTabs().addTab(tab);
			});
			temp.add(addTempItem);

			JMenuItem addTempLoottableType = new JMenuItem(L10N.t("menubar.item.addtemploottabletype"));
			AtomicReference<DataListBuilder> loottabletypes = new AtomicReference<>();
			addTempLoottableType.addActionListener(e -> {
				var gui = new TempLoottableTypeGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(a1 -> {
					if (loottabletypes.get() == null) {
						loottabletypes.set(mcr.createDataList("loottabletypes"));
					}
					loottabletypes.get().appendElement(String.format("""
							%s:
							  other:
							    name: "%s"
							""", a1.typeName, a1.nameModel)).buildAndOutput();
					mcreator.getTabs().closeTab(tab);
				});
				mcreator.getTabs().addTab(tab);
			});
			temp.add(addTempLoottableType);

			JMenuItem export = new JMenuItem(L10N.t("menubar.item.build"));
			export.addActionListener(b -> {
				mcr.initGenerator(mcreator.getGenerator().getGeneratorName(), true);
				mcr.getToolKit().clearGenerator(mcreator.getGenerator().getGeneratorName());
				pluginFolder.mkdirs();
				try {
					String str = new String(
							Objects.requireNonNull(this.getClass().getResourceAsStream("/subplugin.json"))
									.readAllBytes());
					str = String.format(str, uuid);
					Files.copy(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)),
							pluginFolder.toPath().resolve("plugin.json"), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				JOptionPane.showMessageDialog(mcreator, "Build Successful");
			});
			temp.add(export);
		});
	}
}
