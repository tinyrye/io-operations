package com.tinyrye.io;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Loader<T> implements Supplier<T>
{
	private final Supplier<T> loader;
	private final Predicate<T> freshChecker;
	private T value;
	private boolean loaded;

	public Loader(Supplier<T> loader) {
		this.loader = loader;
		this.freshChecker = null;
	}
	
	public Loader(T loadee) {
		this(() -> loadee);
	}

	@Override
	public final T get() {
		if (! loaded || freshChecker == null || ! freshChecker.test(value)) load();
		return value;
	}

	protected void load() {
		value = loader.get();
		loaded = true;
	}
}