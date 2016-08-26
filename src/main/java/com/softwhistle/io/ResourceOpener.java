package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Glorified Supplier but with supporting throws clause for IOException.
 */
@FunctionalInterface
public interface ResourceOpener<I extends Closeable>
{
	I open() throws IOException;
}