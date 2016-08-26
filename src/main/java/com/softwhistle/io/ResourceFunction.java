package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

@FunctionalInterface
public interface ResourceFunction<I extends Closeable, T> {
	public T apply(I resource) throws IOException;
}