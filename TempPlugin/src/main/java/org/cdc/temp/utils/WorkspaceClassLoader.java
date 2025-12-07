package org.cdc.temp.utils;

import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

public class WorkspaceClassLoader extends URLClassLoader {
	private static final Logger log = LogManager.getLogger(WorkspaceClassLoader.class);

	private final Workspace workspace;

	public WorkspaceClassLoader(Workspace workspace) {
		super(new URL[0], null);
		this.workspace = workspace;
		if (workspace.getGenerator().getProjectJarManager() != null) {
			List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager().getClassFileSources();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile()) {
					try {
						addURL(libraryFile.toURI().toURL());
					} catch (MalformedURLException ignored) {
						log.info("Ignored library {}", libraryFile);
					}
				}
			}
		}
		var mods = new File(workspace.getWorkspaceFolder(),"run/mods");
		if (mods.isDirectory()){
			for (File file : Objects.requireNonNull(mods.listFiles())) {
				if (file.isFile()){
					try {
						addURL(file.toURI().toURL());
					} catch (MalformedURLException e) {
						log.info("Ignored mod {}", file);
					}
				}
			}
		}
	}

	public Workspace getWorkspace() {
		return workspace;
	}
}
