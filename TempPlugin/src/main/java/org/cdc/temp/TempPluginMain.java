package org.cdc.temp;

import net.mcreator.io.FileIO;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.util.image.IconUtils;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.MCreatorVersions;
import org.cdc.temp.ui.*;
import org.cdc.temp.utils.PluginUtils;
import org.cdc.temp.utils.WorkspaceClassLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class TempPluginMain extends JavaPlugin {
	private static TempPluginMain INSTANCE;
	public static final Logger LOG = LogManager.getLogger(TempPluginMain.class);

	private final MCreatorPluginFactory mCreatorPluginFactory;

	private final ArrayList<TempElementGUI<?>> tempElementHistory = new ArrayList<>();
	private WorkspaceClassLoader workspaceClassLoader;

	public TempPluginMain(Plugin plugin) {
		super(plugin);

		INSTANCE = this;

		String uuid = String.valueOf(System.currentTimeMillis());
		File pluginFolder = new File(System.getProperty("user.dir"), "plugins/tempplugin" + uuid);
		mCreatorPluginFactory = new MCreatorPluginFactory(pluginFolder);
		mCreatorPluginFactory.setVersion(MCreatorVersions.V_2025_3);

		this.addListener(MCreatorLoadedEvent.class, a -> {
			var mcreator = a.getMCreator();
			if (!(mcreator instanceof ModMaker)) {
				return;
			}

			var mainMenuBar = mcreator.getMainMenuBar();
			Consumer<JMenu> consumer = mainMenuBar::add;

			for (Component component : mainMenuBar.getComponents()) {
				if (component instanceof JMenu menu) {
					if (menu.getText().equals("DevUtils")) {
						consumer = item -> menu.insert(item, 0);
						break;
					}
				}
			}

			JMenu temp = new JMenu("TempPlugin");
			consumer.accept(temp);

			JMenu createTempElements = L10N.menu("menubar.item.createelement");

			JMenuItem addTempItem = createTempItem("item", TempItemGUI::new, mcreator, PluginUtils::doCreateItem);
			createTempElements.add(addTempItem);

			JMenuItem addTempAchievement = createTempItem("achievement", TempAchievementGUI::new, mcreator,
					PluginUtils::doCreateAchievement);
			createTempElements.add(addTempAchievement);

			JMenuItem addTempStructure = createTempItem("structure", TempStructureGUI::new, mcreator,
					PluginUtils::doCreateStructure);
			createTempElements.add(addTempStructure);

			JMenuItem addTempBiome = createTempItem("biome", TempBiomeGUI::new, mcreator, PluginUtils::doCreateBiome);
			createTempElements.add(addTempBiome);

			JMenuItem addTempPotion = createTempItem("potion",TempPotionGUI::new,mcreator,PluginUtils::doCreatePotion);
			createTempElements.add(addTempPotion);

			JMenuItem addTempPotionEffect = createTempItem("potioneffect",TempPotionEffectGUI::new,mcreator,PluginUtils::doCreatePotionEffect);
			createTempElements.add(addTempPotionEffect);

			JMenuItem addTempParticle = createTempItem("particle",TempParticleGUI::new,mcreator,PluginUtils::doCreateParticle);
			createTempElements.add(addTempParticle);

			temp.add(createTempElements);

			JMenuItem export = new JMenuItem(L10N.t("menubar.item.build"));
			export.setIcon(UIRES.get("16px.build"));
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
				JOptionPane.showMessageDialog(mcreator, "Build Successful " + uuid);
			});
			temp.add(export);

			JMenu tempHistory = L10N.menu("menubar.item.temphistory");
			tempHistory.addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) {
					tempHistory.removeAll();
					for (TempElementGUI<?> elementGUI : tempElementHistory) {
						JMenuItem menuItem = new JMenuItem(elementGUI.getViewName());
						menuItem.setIcon(IconUtils.resize(elementGUI.getViewIcon(), 24, 24));
						menuItem.addActionListener(action -> {
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
					deleteTempPlugin.removeAll();
					for (File file : Objects.requireNonNull(pluginFolder.getParentFile().listFiles())) {
						if (file.isDirectory() && file.getName().startsWith("tempplugin")) {
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

	private void reloadWorkspaceClassLoader(Workspace workspace){
		if (workspaceClassLoader != null){
			if (workspaceClassLoader.getWorkspace().equals(workspace)){
				return;
			}
		}
		workspaceClassLoader = new WorkspaceClassLoader(workspace);
	}

	public MCreatorPluginFactory getmCreatorPluginFactory() {
		return mCreatorPluginFactory;
	}

	public WorkspaceClassLoader getWorkspaceClassLoader() {
		return workspaceClassLoader;
	}

	public static TempPluginMain getInstance() {
		return INSTANCE;
	}

	private <F, T extends TempElementGUI<F>> JMenuItem createTempItem(String type, Function<MCreator, T> function,
			MCreator mcreator, Function<F, Runnable> consumer) {
		var item = new JMenuItem(L10N.t("menubar.item.addtemp", L10N.t("modelement." + type)));
		item.setIcon(UIRES.get("mod_types." + type));
		item.addActionListener(e -> {
			reloadWorkspaceClassLoader(mcreator.getWorkspace());
			T gui = function.apply(mcreator);
			var tab = new MCreatorTabs.Tab(gui);
			gui.setOnSaved(d -> {
				gui.getRedo().run();
				try {
					gui.setRedo(consumer.apply(d));
				} catch (RuntimeException runtimeException){
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					PrintStream printStream = new PrintStream(byteArrayOutputStream);
					runtimeException.printStackTrace(printStream);
					JOptionPane.showMessageDialog(null,byteArrayOutputStream.toString(),"Error",JOptionPane.WARNING_MESSAGE);
				}
				mcreator.getTabs().closeTab(mcreator.getTabs().getCurrentTab());
				tempElementHistory.add(gui);
			});
			mcreator.getTabs().addTab(tab);
		});
		return item;
	}
}
