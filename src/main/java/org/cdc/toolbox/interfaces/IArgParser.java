package org.cdc.toolbox.interfaces;

import java.lang.instrument.Instrumentation;

public interface IArgParser {
	void parse(Instrumentation instrumentation,String... arg);
}
