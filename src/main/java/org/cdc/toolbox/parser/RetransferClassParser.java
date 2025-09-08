package org.cdc.toolbox.parser;

import org.cdc.toolbox.interfaces.IArgParser;
import org.cdc.toolbox.uitls.ProcedureClassTransformer;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RetransferClassParser implements IArgParser {
	@Override public void parse(Instrumentation instrumentation, String... arg) {
		final File buildPath = new File(arg[1]);
		instrumentation.addTransformer(new ProcedureClassTransformer(buildPath,instrumentation,arg[2]),true);

		ArrayList<Class<?>> classes = new ArrayList<>();
		for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
			if (instrumentation.isModifiableClass(loadedClass)) {
				if (loadedClass.getSimpleName().contains(arg[2])) {
					String name = loadedClass.getName().replace('.', '/');
					File classFile = new File(buildPath, name+".class");
					if (classFile.exists())
						classes.add(loadedClass);
				}
			}
		}
		if (!classes.isEmpty()) {
			System.out.println(classes.stream().map(Class::getName).collect(Collectors.joining(",")));
			try {
				instrumentation.retransformClasses(classes.toArray(new Class[0]));
			} catch (UnmodifiableClassException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
