package com.softwhistle.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Operations
{
	private static final ErrorHandler NOOP_HANDLER = new ErrorHandler.Basic();

	public static void run(MultipleResourceOperation op) {
		run(op, NOOP_HANDLER);
	}

	public static void run(MultipleResourceOperation op, ErrorHandler handler) {
		List<Closeable> resources = new ArrayList<Closeable>();
		try { resources = op.doOperation(); }
		catch (IOException ex) { handleOnOperation(handler, ex); }
		finally { if (resources != null) close(handler, resources); }
	}

	public static void run(SingleResourceOperation op) {
		run(op, NOOP_HANDLER);
	}

	public static void run(SingleResourceOperation op, ErrorHandler handler) {
		Closeable resource = null;
		try { resource = op.doOperation(); }
		catch (IOException ex) { handleOnOperation(handler, ex); }
		finally { if (resource != null) close(handler, resource); }
	}

	public static void run(HiddenResourceOperation op) {
		run(op, NOOP_HANDLER);
	}

	public static void run(HiddenResourceOperation op, ErrorHandler handler) {
		try { op.doOperation(); } catch (IOException ex) { handleOnOperation(handler, ex); }
	}

	public static <I extends Closeable,T> T openThenWith(ResourceOpener<I> resourceSupplier, ResourceFunction<I,T> withFunction) {
		return openThenWith(resourceSupplier, withFunction, () -> null);
	}

	public static <I extends Closeable,T> T openThenWith(ResourceOpener<I> resourceSupplier, ResourceFunction<I,T> withFunction, AlternativeErrorHandler<T> handler) {
		I resource = null;
		try { resource = resourceSupplier.open(); }
		catch (IOException ex) { handleOnOpen(handler, ex); }

		if (resource != null) return with(resource, withFunction, handler);
		else throw new RuntimeException("No resource found for operation.");
	}

	public static <I extends Closeable,T> T with(I resource, ResourceFunction<I,T> withFunction) {
		return with(resource, withFunction, () -> null);
	}

	public static <I extends Closeable,T> T with(I resource, ResourceFunction<I,T> withFunction, AlternativeErrorHandler<T> handler) {
		try { return withFunction.apply(resource); }
		catch (IOException ex) { return handleOnOperation(handler, ex); }
		finally { close(handler, resource); }
	}

	/* public static <I extends Closeable> void with(I resource, ResourceConsumer<I> withConsumer) {
		with(resource, withConsumer, NOOP_HANDLER);
	}

	public static <I extends Closeable> void with(I resource, ResourceConsumer<I> withConsumer, ErrorHandler handler) {
		try { withConsumer.accept(resource); }
		catch (IOException ex) { handleOnOperation(handler, ex); }
		finally { close(handler, resource); }
	} */

	public static void close(ErrorHandler handler, Closeable ... resources) {
		for (Closeable res: resources) {
			try { res.close(); }
			catch (IOException ex) { handleOnClose(handler, ex); }
		}
	}

	public static void close(ErrorHandler handler, Iterable<Closeable> resources) {
		for (Closeable res: resources) {
			try { res.close(); }
			catch (IOException ex) { handleOnClose(handler, ex); }
		}
	}

	protected static void handleOnOpen(ErrorHandler handler, IOException ex) {
		if (! handler.ignoreOnOpen(ex)) throw new RuntimeException(ex);
	}

	protected static void handleOnOperation(ErrorHandler handler, IOException ex) {
		if (! handler.ignoreFromOperation(ex)) throw new RuntimeException(ex);
	}

	protected static <T> T handleOnOperation(AlternativeErrorHandler<T> handler, IOException ex) {
		if (! handler.ignoreFromOperation(ex)) throw new RuntimeException(ex);
		else return handler.alternative();
	}

	protected static void handleOnClose(ErrorHandler handler, IOException ex) {
		if (! handler.ignoreOnClose(ex)) throw new RuntimeException(ex);
	}
}