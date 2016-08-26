package com.softwhistle.io;

import java.io.Closeable;
import java.io.IOException;

@FunctionalInterface
public interface ResourceConsumer<I extends Closeable> {
	public void accept(I resource) throws IOException;
}