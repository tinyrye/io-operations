package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Glorified {@link java.util.function.Function} but with IOException throws clause.
 */
@FunctionalInterface
public interface ResourceFunction<I extends Closeable, T> {
	public T apply(I resource) throws IOException;
}