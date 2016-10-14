package com.softwhistle.io;

import static com.softwhistle.io.Phase.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Utilities to guide I/O operation lifecycle:
 * Typically there are three stages:
 * 1.opening resources
 * 2.using resources
 * 3.closing resources no longer needed
 * In addition to the phases the lifecycle will incur exceptions; consumers
 * will want to hide the typed exception and provide alternative behavior
 * 
 */
public class Operations
{
	/**
	 * This basic method is useful for hiding <code>IOException</code> since it is
	 * typed exception that requires catches around invocations.
	 * 
	 * Essentially,
	 * <pre>
	 *   Operations.run(() -> {
	 *     do some stuff with resources like writing to output stream
	 *   });
	 * </pre>
	 * will replace
	 * <pre>
	 *   try {
	 *     do some stuff with resources like writing to output stream
	 *   }
	 *   catch (IOException ex) {
	 *     throw new RuntimeException(ex);
	 *   }
	 * </pre>
	 * 
	 * If you plan on making use of the IOException rather than ignoring it or even wrapping
	 * it in an unchecked exception it then this method is not much use to you unless you'd
	 * rather silently handle the exception.  In that case use {@link run(Operation,ErrorHandler)}
	 * instead and implement the error handler to suit your handling purposes.
	 */
	public static void run(Operation op) {
		run(op, DEFER_HANDLER);
	}

	/**
	 * 
	 */
	public static void run(Operation op, ErrorHandler handler) {
		try { op.run(); } catch (IOException ex) { handleOnOperation(handler, ex); }
	}

	public static <I extends Closeable> void openAndRun(ResourceOpener<I> resourceSupplier, ResourceConsumer<I> withConsumer) {
		openAndRun(resourceSupplier, withConsumer, DEFER_HANDLER);
	}

	public static <I extends Closeable> void openAndRun(ResourceOpener<I> resourceSupplier, ResourceConsumer<I> withConsumer, ErrorHandler handler) {
		run(open(resourceSupplier, handler), withConsumer, handler);
	}

	public static <I extends Closeable> void run(I resource, ResourceConsumer<I> op) {
		run(resource, op, DEFER_HANDLER);
	}

	public static <I extends Closeable> void run(I resource, ResourceConsumer<I> op, ErrorHandler handler) {
		try { op.accept(resource); }
		catch (IOException ex) { handleOnOperation(handler, ex); }
		finally { if (resource != null) close(handler, resource); }
	}

	public static <I extends Closeable,T> T openAndTransform(ResourceOpener<I> resourceSupplier, ResourceFunction<I,T> transformFunction) {
		return openAndTransform(resourceSupplier, transformFunction, DEFER_HANDLER, null);
	}

	public static <I extends Closeable,T> T openAndTransform(ResourceOpener<I> resourceSupplier, ResourceFunction<I,T> transformFunction, ErrorHandler handler, Supplier<T> alternative) {
		I resource = openOpt(resourceSupplier, handler);
		if (resource == null) {
			if (alternative != null) return alternative.get();
			else throw new NoSuchElementException("No alternative to value to supply en lieu of unavailable resource.");
		}
		else return transform(resource, transformFunction, handler, alternative);
	}

	public static <I extends Closeable,T> T transform(I resource, ResourceFunction<I,T> transformFunction) {
		return transform(resource, transformFunction, DEFER_HANDLER, null);
	}

	public static <I extends Closeable,T> T transform(I resource, ResourceFunction<I,T> transformFunction, ErrorHandler handler, Supplier<T> alternative) {
		try { return transformFunction.apply(resource); }
		catch (IOException ex) { return handleOnOperation(handler, alternative, ex); }
		finally { close(handler, resource); }
	}
	
	public static <I extends Closeable> I open(ResourceOpener<I> resourceSupplier) {
		return open(resourceSupplier, DEFER_HANDLER);
	}

	public static <I extends Closeable> I open(ResourceOpener<I> resourceSupplier, ErrorHandler handler) {
		I resource = openOpt(resourceSupplier, handler);
		if (resource == null) throw new RuntimeException("No resource available for operation.");
		else return resource;
	}

	public static <I extends Closeable> I openOpt(ResourceOpener<I> resourceSupplier, ErrorHandler handler) {
		try { return resourceSupplier.open(); }
		catch (IOException ex) { handleOnOpen(handler, ex); return null; }
	}

	public static void close(ErrorHandler handler, Iterable<Closeable> resources) {
		resources.forEach((res) -> close(res, handler));
	}

	public static void close(ErrorHandler handler, Closeable ... resources) {
		for (Closeable resource: resources) {
			close(resource, handler);
		}
	}

	public static void close(Closeable resource, ErrorHandler handler) {
		try { resource.close(); }
		catch (IOException ex) { handleOnClose(handler, ex); }
	}

	protected static void handleOnOpen(ErrorHandler handler, IOException ex) {
		if (! handler.handle(ex, OPEN)) throw new RuntimeException(ex);
	}

	protected static void handleOnOperation(ErrorHandler handler, IOException ex) {
		if (! handler.handle(ex, PERFORM)) throw new RuntimeException(ex);
	}

	protected static <T> T handleOnOperation(ErrorHandler handler, Supplier<T> alternative, IOException ex) {
		if (! handler.handle(ex, PERFORM)) throw new RuntimeException(ex);
		else if (alternative != null) return alternative.get();
		else throw new NoSuchElementException("No alternative to value to supply en lieu of failed operation.");
	}

	protected static void handleOnClose(ErrorHandler handler, IOException ex) {
		if (! handler.handle(ex, CLOSE)) throw new RuntimeException(ex);
	}

	private static final ErrorHandler DEFER_HANDLER = (ex, phase) -> false;
}