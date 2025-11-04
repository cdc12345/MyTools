package org.cdc.interfaces;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.generator.template.TemplateGenerator;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

public class GeneratorImpl implements IGenerator{

	private final Generator origin;

	public GeneratorImpl(Generator origin){
		this.origin = origin;
	}

	@Override public List<GeneratorTemplate> getModBaseGeneratorTemplatesList() {
		try {
			return origin.getModBaseGeneratorTemplatesList();
		} catch (Throwable throwable){
			var lookup = MethodHandles.publicLookup();
			var methodType = MethodType.methodType(List.class, Boolean.TYPE);
			try {
				return (List<GeneratorTemplate>) lookup.findVirtual(origin.getClass(),"getModBaseGeneratorTemplatesList",methodType).invoke(origin,false);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override public TemplateGenerator getTemplateGeneratorFromName(String templates) {
		return origin.getTemplateGeneratorFromName(templates);
	}

	@Override public File getGeneratorPackageRoot() {
		return origin.getGeneratorPackageRoot();
	}

	@Override public File getLangFilesRoot() {
		return origin.getLangFilesRoot();
	}

	@Override public GeneratorConfiguration getGeneratorConfiguration() {
		return origin.getGeneratorConfiguration();
	}
}
