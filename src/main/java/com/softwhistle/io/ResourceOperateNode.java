package com.softwhistle.io;

import java.io.Closeable;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class ResourceOperateNode<I extends Closeable>
{
	private final I resource;
	private final ErrorHandler errorHandler;
	private boolean closed = false;

	public ResourceOperateNode() {
		this.resource = null;
		this.errorHandler = null;
	}
	public ResourceOperateNode(I resource) {
		this.resource = resource;
		this.errorHandler = null;
	}
	public ResourceOperateNode(I resource, ErrorHandler errorHandler) {
		this.resource = resource;
		this.errorHandler = errorHandler;
	}

	public <T> T apply(ResourceFunction<I,T> applier) {
		if (closedOrNoResource()) throw new NoSuchElementException("No resource or resource is closed.");
		try { return Operations.apply(resource, applier, errorHandler); }
		finally { close(); }
	}
	public <T> T applyOpt(ResourceFunction<I,T> applier, Supplier<T> alternative) {
		if (closedOrNoResource()) return alternative.get();
		try { return Operations.apply(resource, applier, alternative, errorHandler); }
		finally { close(); }
	}
	public void accept(ResourceConsumer<I> consumer) {
		if (closedOrNoResource()) throw new NoSuchElementException("No resource or resource is closed.");
		try { Operations.accept(resource, consumer, errorHandler); }
		finally { close(); }
	}

	public void close() {
		Operations.close(resource, errorHandler);
		closed = true;
	}
	public boolean closedOrNoResource() {
		return (resource == null) || closed;
	}
}