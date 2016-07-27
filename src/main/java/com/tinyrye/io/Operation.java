package com.tinyrye.io;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Essentially a wrapping over an IO function in order call the operation within
 * a larger context such as a try-catch.
 */
@FunctionalInterface
public interface Operation<T>
{
	public static <T> Operation<T> of(Function<List<Closeable>,T> performer) {
		return (resourceBin) -> performer.apply(resourceBin);
	}

	public abstract T perform(List<Closeable>> resourceBin) throws IOException;

	/**
	 * 
	 */
	public default T run() {
		return run(false, null, null);
	}

	/**
	 * 
	 */
	public default T run(boolean keepOpen, Consumer<IOException> mainErrorHandler, Consumer<IOException> closeErrorHandler)
	{
		T output = null;
		List<Closeable> resources = new ArrayList<Closeable>();
		try { output = perform(resources); }
		catch (IOException ex) {
			if (mainErrorHandler != null) mainErrorHandler.accept(ex);
			else throw new RuntimeException(ex);
		}
		if (! keepOpen)
		{
			resources.forEach(resource -> {
				try { resource.close(); }
				catch (IOException ex) {
					if (closeErrorHandler != null) closeErrorHandler.accept(ex);
					else throw new RuntimeException(ex);
				}
			});
		}
		return output;
	}
}