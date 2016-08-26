package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface HiddenResourceOperation {
	public void doOperation() throws IOException;
}