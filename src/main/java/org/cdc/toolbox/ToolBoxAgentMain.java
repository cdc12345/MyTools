package org.cdc.toolbox;

import org.cdc.toolbox.interfaces.IArgParser;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ToolBoxAgentMain {

	public static void agentmain(String args, Instrumentation instrumentation) throws UnmodifiableClassException {

		System.out.println("Hi,It is an agent parameter:" + args);

		String[] argArray = args.split(">");
		if (argArray.length >= 1) {
			try {
				Class<?> parser = Class.forName(argArray[0]);
				IArgParser argParser;
				if (!parser.isInstance(IArgParser.class)) {
					Object object = parser.getConstructor().newInstance();

					argParser = (IArgParser) Proxy.newProxyInstance(parser.getClassLoader(), new Class[] { IArgParser.class },
							(proxy, method, args1) -> {
								Method target = parser.getMethod(method.getName(),method.getParameterTypes());
								return target.invoke(object, args1);
							});
				} else {
					argParser = (IArgParser) parser.getConstructor().newInstance();
				}

				argParser.parse(instrumentation, argArray);

			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
					 IllegalAccessException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
