package org.cdc.interfaces;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.ui.*;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceFolderManager;
import net.mcreator.workspace.elements.ModElementManager;

public interface IMCreator {

	MCreatorTabs getTabs();

	Workspace getWorkspace();

	MCreator getOrigin();

	GeneratorConfiguration getGeneratorConfiguration();

	MCreatorApplication getApplication();

	StatusBar getStatusBar();

	WorkspaceFolderManager getFolderManager();

	MainMenuBar getMainMenuBar();

	ModElementManager getModElementManager();

	GradleConsole getGradleConsole();
}
