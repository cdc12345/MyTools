package org.cdc.temp;

import net.mcreator.io.FileIO;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.variants.modmaker.ModMaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.temp.ui.*;
import org.cdc.temp.utils.PluginUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;

public class TempPluginMain extends JavaPlugin {
	private static TempPluginMain INSTANCE;
	public static final Logger LOG = LogManager.getLogger(TempPluginMain.class);

	private final MCreatorPluginFactory mCreatorPluginFactory;

	private final ArrayList<TempElementGUI<?>> tempElementHistory = new ArrayList<>();

	public TempPluginMain(Plugin plugin) {
		super(plugin);

		INSTANCE = this;

		String uuid = String.valueOf(System.currentTimeMillis());
		File pluginFolder = new File(System.getProperty("user.dir"), "plugins/tempplugin" + uuid);
		mCreatorPluginFactory = new MCreatorPluginFactory(pluginFolder);

		this.addListener(MCreatorLoadedEvent.class, a -> {
			var mcreator = a.getMCreator();
			if (!(mcreator instanceof ModMaker)) {
				return;
			}

			var mainMenuBar = mcreator.getMainMenuBar();

			JMenu temp = new JMenu("TempPlugin");
			mainMenuBar.add(temp);

			JMenuItem addTempItem = new JMenuItem(L10N.t("menubar.item.addtempitem"));
			addTempItem.addActionListener(b -> {
				var gui = new TempItemGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(c->{
					gui.getRedo().run();
					gui.setRedo(PluginUtils.doCreateItem(c));
					mcreator.getTabs().closeTab(mcreator.getTabs().getCurrentTab());
					tempElementHistory.add(gui);
				});
				mcreator.getTabs().addTab(tab);

			});
			temp.add(addTempItem);

			JMenuItem addTempAchievement = new JMenuItem(L10N.t("menubar.item.addtempachievement"));
			addTempAchievement.addActionListener(c->{
				var gui = new TempAchievementGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(d->{
					gui.getRedo().run();
					gui.setRedo(PluginUtils.doCreateAchievement(d));
					mcreator.getTabs().closeTab(mcreator.getTabs().getCurrentTab());
					tempElementHistory.add(gui);
				});
				mcreator.getTabs().addTab(tab);
			});
			temp.add(addTempAchievement);

			JMenuItem addTempStructure = new JMenuItem(L10N.t("menubar.item.addtempstructure"));
			addTempStructure.addActionListener(c->{
				var gui = new TempStructureGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(d->{
					gui.getRedo().run();
					gui.setRedo(PluginUtils.doCreateStructure(d));
					mcreator.getTabs().closeTab(mcreator.getTabs().getCurrentTab());
					tempElementHistory.add(gui);
				});
				mcreator.getTabs().addTab(tab);
			});
			temp.add(addTempStructure);

			JMenuItem addTempBiome = new JMenuItem(L10N.t("menubar.item.addtempbiome"));
			addTempBiome.addActionListener(c->{
				var gui = new TempBiomeGUI(mcreator);
				var tab = new MCreatorTabs.Tab(gui);
				gui.setOnSaved(d->{
					gui.getRedo().run();
					gui.setRedo(PluginUtils.doCreateBiome(d));
					mcreator.getTabs().closeTab(mcreator.getTabs().getCurrentTab());
					tempElementHistory.add(gui);
				});
				mcreator.getTabs().addTab(tab);
			});
			temp.add(addTempBiome);

			JMenuItem export = new JMenuItem(L10N.t("menubar.item.build"));
			export.addActionListener(b -> {
				mCreatorPluginFactory.initGenerator(mcreator.getGenerator().getGeneratorName(), true);
				mCreatorPluginFactory.getToolKit().clearGenerator(mcreator.getGenerator().getGeneratorName());
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

			JMenu tempHistory = L10N.menu("menubar.item.temphistory");
			tempHistory.addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) {
					tempHistory.removeAll();
					for (TempElementGUI<?> elementGUI : tempElementHistory) {
						JMenuItem menuItem = new JMenuItem(elementGUI.getViewName());
						menuItem.addActionListener(action->{
							var tab = new MCreatorTabs.Tab(elementGUI);
							mcreator.getTabs().addTab(tab);
							tempElementHistory.remove(elementGUI);
						});
						tempHistory.add(menuItem);
					}
				}
			});
			temp.add(tempHistory);

			JMenu deleteTempPlugin = L10N.menu("menubar.item.deleteTempPlugin");
			deleteTempPlugin.addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) {
					for (File file : Objects.requireNonNull(pluginFolder.getParentFile().listFiles())) {
						deleteTempPlugin.removeAll();
						if (file.isDirectory() && file.getName().startsWith("tempplugin")){
							var menuItem = new JMenuItem(file.getName());
							menuItem.addActionListener(actionEvent -> {
								FileIO.deleteDir(file);
							});
							deleteTempPlugin.add(menuItem);
						}
					}
				}
			});
			temp.add(deleteTempPlugin);
		});
	}

	public MCreatorPluginFactory getmCreatorPluginFactory() {
		return mCreatorPluginFactory;
	}

	public static TempPluginMain getInstance() {
		return INSTANCE;
	}
}
