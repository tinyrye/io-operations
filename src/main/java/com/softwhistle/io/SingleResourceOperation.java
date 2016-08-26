package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

@FunctionalInterface
public interface SingleResourceOperation
{
	/**
	 * @param resourcesToClose Add your closeables to this list if you want the operation runner
	 * to close it for you.
	 */
	public Closeable doOperation() throws IOException;
}