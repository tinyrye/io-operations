package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Glorified {@link java.lang.Runnable} but with IOException throws clause.
 */
@FunctionalInterface
public interface Operation
{
	/**
	 * This type of use case facillitates error handling even when the operation takes
	 * ownership of ensulating what the resources are and whether they are to be closed.
	 */
	public void run() throws IOException;
}