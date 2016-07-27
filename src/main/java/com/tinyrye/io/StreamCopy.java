package com.tinyrye.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class StreamCopy implements Operation<Void>
{
	private Supplier<InputStream> source;
	private Supplier<OutputStream> destination;
	private int chunkSpecReadSize = 4096; 
	
	public StreamCopy(InputStream source, OutputStream destination) {
		this(() -> source, () -> destination);
	}

	public StreamCopy(Supplier<InputStream> source, Supplier<OutputStream> destination) {
		this.source = source;
		this.destination = destination;
	}
	
	@Override
	public void perform(List<Closeable> resourceBin) throws IOException {
		InputStream source = this.source.get();
		OutputStream destination = this.destination.get();
		resourceBin.add(source);
		resourceBin.add(destination);
		byte[] transferChunk = new byte[chunkSpecReadSize];
		int chunkReadSize = -1;
		while ((chunkReadSize = source.read(transferChunk, 0, chunkSpecReadSize)) != -1) {
			destination.write(transferChunk, 0, chunkReadSize);
		}
	}
		
	public int readChunksAt() {
		return chunkSpecReadSize;
	}

	public StreamCopy readChunksAt(int chunkSpecReadSize) {
		this.chunkSpecReadSize = chunkSpecReadSize;
		return this;
	}
}