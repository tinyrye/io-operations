package com.tinyrye.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamToByteArray extends Operation
{
	public static final int DEFAULT_PREALLOC = 32768;
	public static final int DEFAULT_BURST = 1024;
	
	private ResourceLoader<InputStream> source;
	private byte[] sourceBytes;
	private int sourceCursor = -1;
	private int burstSize = DEFAULT_BURST;
	private int reallocSize = -1;

	public InputStreamToByteArray(InputStream source) {
		this(new ResourceLoader<InputStream>().set(source));
	}

	public InputStreamToByteArray(ResourceLoader<InputStream> source) {
		this.source = source;
	}

	public InputStreamToByteArray readBurstAt(int burstSize) {
		this.burstSize = burstSize;
		return this;
	}

	public InputStreamToByteArray reallocBy(int reallocSize) {
		this.reallocSize = reallocSize;
		return this;
	}
	
	public InputStreamToByteArray prealloc(int preallocSize) {
		sourceBytes = new byte[preallocSize];
		return this;
	}

	@Override
	protected void performOperation() throws IOException {
		initAlloc();
		while (ensureEnoughAlloc() && readBurst()) { }
		truncate();
	}
	
	@Override
	public void close() throws IOException {
		if (source != null) {
			source.get().close();
			source = null;
		}
	}
	
	public byte[] getBytes() {
		return sourceBytes;
	}
	
	protected void initAlloc() {
		if (sourceBytes == null) sourceBytes = new byte[DEFAULT_PREALLOC];
		if (reallocSize == -1) reallocSize = burstSize;
		sourceCursor = 0;
	}
	
	protected boolean ensureEnoughAlloc() {
		int remainingRoomInSource = (sourceBytes.length - sourceCursor);
		if (remainingRoomInSource < burstSize) realloc();
		return true;
	}
	
	public boolean readBurst() throws IOException {
		int readLen = source.get().read(sourceBytes, sourceCursor, burstSize);
		if (readLen > 0) sourceCursor += readLen;
		return (readLen >= 0);
	}
	
	protected void realloc()
	{
		byte[] existingGen = sourceBytes;
		sourceBytes = new byte[existingGen.length + reallocSize];
		for (int i = 0; i < sourceBytes.length; i++) {
			sourceBytes[i] = (byte) 0;
		}
		System.arraycopy(existingGen, 0, sourceBytes, 0, existingGen.length);
	}

	/**
	 * Trim to exact byte length of source.
	 */
	protected void truncate()
	{
		if (sourceCursor != sourceBytes.length - 1) {
			byte[] existingGen = sourceBytes;
			sourceBytes = new byte[sourceCursor + 1];
			System.arraycopy(existingGen, 0, sourceBytes, 0, sourceCursor + 1);
		}
	}
}