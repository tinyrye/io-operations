package com.tinyrye.io;

import java.io.File;
import java.io.IOException;

public class ResourceLoader<T>
{
	private boolean loaded;
	private T value;

	protected void load() throws IOException {
		/* noop; extend to load value and store by calling set(). */
	}

	public final T get() throws IOException {
		if (! loaded) load();
		return value;
	}
	
	public final ResourceLoader<T> set(T value) {
		this.value = value;
		this.loaded = true;
		return this;
	}
}