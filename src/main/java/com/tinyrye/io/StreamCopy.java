package com.tinyrye.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamCopy extends Operation
{
	private ResourceLoader<InputStream> source;
	private ResourceLoader<OutputStream> destination;
	private int chunkSpecReadSize = 4096; 
	
	public StreamCopy(InputStream source, OutputStream destination) {
		this(new ResourceLoader<InputStream>().set(source), new ResourceLoader<OutputStream>().set(destination));
	}

	public StreamCopy(ResourceLoader<InputStream> source, ResourceLoader<OutputStream> destination) {
		this.source = source;
		this.destination = destination;
	}
		
	public int readChunksAt() {
		return chunkSpecReadSize;
	}

	public StreamCopy readChunksAt(int chunkSpecReadSize) {
		this.chunkSpecReadSize = chunkSpecReadSize;
		return this;
	}
	
	@Override
	protected void performOperation() throws IOException
	{
		byte[] transferChunk = new byte[chunkSpecReadSize];
		int chunkReadSize = -1;
		while ((chunkReadSize = source.get().read(transferChunk, 0, chunkSpecReadSize)) != -1) {
			destination.get().write(transferChunk, 0, chunkReadSize);
		}
	}
	
	@Override
	public void close() throws IOException {
		if (source != null) { source.get().close(); source = null; }
		if (destination != null) { destination.get().close(); destination = null; }
	}
}