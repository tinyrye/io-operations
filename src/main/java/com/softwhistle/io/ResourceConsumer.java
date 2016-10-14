package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Glorified {@link java.util.function.Consumer} but with IOException throws clause.
 */
@FunctionalInterface
public interface ResourceConsumer<I extends Closeable> {
	public void accept(I resource) throws IOException;
}