package org.cdc.interfaces;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.generator.template.TemplateGenerator;

import java.io.File;
import java.util.List;

public interface IGenerator {
	List<GeneratorTemplate> getModBaseGeneratorTemplatesList();

	TemplateGenerator getTemplateGeneratorFromName(String templates);

	File getGeneratorPackageRoot();

	File getLangFilesRoot();

	GeneratorConfiguration getGeneratorConfiguration();
}
