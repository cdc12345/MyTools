package net.nerdypuzzle.forgemixins;

import freemarker.template.Template;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.template.InlineTemplatesHandler;
import net.mcreator.generator.template.base.BaseDataModelProvider;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.nerdypuzzle.forgemixins.element.Mixin;
import net.nerdypuzzle.forgemixins.element.MixinGUI;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

import static net.mcreator.element.ModElementTypeLoader.register;

public class MixinPluginMain extends JavaPlugin{
    public static final Logger LOG = LogManager.getLogger("Mixin");

    public static ModElementType<Mixin> mixinModElementType;

    public MixinPluginMain(Plugin plugin) {
        super(plugin);
        JavaPlugin parent = this;
		parent.addListener(MCreatorLoadedEvent.class, event -> {
			Generator currentGenerator = event.getMCreator().getGenerator();
			if (currentGenerator != null) {
				var generatorConfig = currentGenerator.getGeneratorConfiguration();

				if (generatorConfig.getGeneratorFlavor() == GeneratorFlavor.FORGE) {
					Set<String> fileNames = PluginLoader.INSTANCE.getResourcesInPackage(currentGenerator.getGeneratorName() + ".templates.modbase");
					Map<String, Object> dataModel = (new BaseDataModelProvider(event.getMCreator().getWorkspace().getGenerator())).provide();
                    LOG.info(fileNames);
                    for (String file : fileNames) {
                        if (file.endsWith("mixin.gradle")) {
                            LOG.info(file);
                            InputStream stream = PluginLoader.INSTANCE.getResourceAsStream(file);
                            File generatorFile = new File(event.getMCreator().getWorkspaceFolder(), file.replace(currentGenerator.getGeneratorName() + "/templates/modbase/", ""));
							File buildGradle = new File(event.getMCreator().getWorkspaceFolder(),"build.gradle");
							try {
                                String contents = IOUtils.toString(stream, StandardCharsets.UTF_8);
                                Template freemarkerTemplate = InlineTemplatesHandler.getTemplate(contents);
                                StringWriter stringWriter = new StringWriter();
                                freemarkerTemplate.process(dataModel, stringWriter, InlineTemplatesHandler.getConfiguration().getObjectWrapper());
                                FileIO.writeStringToFile(stringWriter.getBuffer().toString(), generatorFile);
								if (!FileIO.readFileToString(buildGradle).contains("apply from: 'mixin.gradle'"))
									Files.writeString(buildGradle.toPath(), """
											if (file("mixin.gradle").exists())
											    apply from: 'mixin.gradle'
											""", StandardOpenOption.APPEND);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
				}
			}
		});

		parent.addListener(PreGeneratorsLoadingEvent.class, event -> {
            mixinModElementType = new ModElementType<>("newmixin", null, MixinGUI::new, Mixin.class);
			register(mixinModElementType);
		});

		LOG.info("Mixins plugin was loaded");
	}



}