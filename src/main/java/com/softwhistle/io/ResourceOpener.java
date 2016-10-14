package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Glorified {@link java.util.function.Supplier} but with IOException throws clause.
 */
@FunctionalInterface
public interface ResourceOpener<I extends Closeable>
{
	I open() throws IOException;
}