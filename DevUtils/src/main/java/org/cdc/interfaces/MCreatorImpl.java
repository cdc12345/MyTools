package org.cdc.interfaces;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.ui.*;
import net.mcreator.ui.browser.WorkspaceFileBrowser;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceFolderManager;
import net.mcreator.workspace.elements.ModElementManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MCreatorImpl implements IMCreator{

	private final MCreator origin;

	public MCreatorImpl(MCreator origin){
		this.origin = origin;
	}

	@Override public MCreatorTabs getTabs() {
		try {
			return origin.getTabs();
		} catch (Throwable throwable){
			Method method;
			try {
				method = origin.getClass().getMethod("getMCreatorTabs");
				return (MCreatorTabs) method.invoke(origin);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		}
	}

	@Override public Workspace getWorkspace() {
		return origin.getWorkspace();
	}

	@Override public MCreator getOrigin() {
		return origin;
	}

	@Override public GeneratorConfiguration getGeneratorConfiguration() {
		return origin.getGeneratorConfiguration();
	}

	@Override public MCreatorApplication getApplication() {
		return origin.getApplication();
	}

	@Override public StatusBar getStatusBar() {
		return origin.getStatusBar();
	}

	@Override public WorkspaceFolderManager getFolderManager() {
		return origin.getFolderManager();
	}

	@Override public MainMenuBar getMainMenuBar() {
		return origin.getMainMenuBar();
	}

	public GradleConsole getGradleConsole() {return origin.getGradleConsole();}

	public ModElementManager getModElementManager(){
		return origin.getModElementManager();
	}

	@Override public IGenerator getGenerator() {
		return new GeneratorImpl(origin.getGenerator());
	}

	@Override public WorkspaceFileBrowser getProjectBrowser() {
		return origin.getProjectBrowser();
	}
}
