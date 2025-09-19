package org.cdc.toolbox.uitls;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProcedureClassTransformer implements ClassFileTransformer {

	private final File buildPath;
	private final Instrumentation instrumentation;
	private final String elementName;

	public ProcedureClassTransformer(File compileResult, Instrumentation instrumentation,String elementName){
		this.buildPath = compileResult;
		this.instrumentation = instrumentation;
		this.elementName = elementName;
	}

	@Override
	public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		File classFile = new File(buildPath, className+".class");
		if (classFile.exists() && className.contains(elementName)){
			try {
				CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(()->{
					instrumentation.removeTransformer(ProcedureClassTransformer.this);
				});
				return Files.readAllBytes(classFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}
}
