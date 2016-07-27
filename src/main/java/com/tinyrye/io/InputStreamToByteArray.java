package com.tinyrye.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class InputStreamToByteArray implements Operation<Byte[]>
{
	public static final int DEFAULT_PREALLOC = 32768;
	public static final int DEFAULT_BURST = 1024;
	
	private Supplier<InputStream> source;
	private int burstSize = DEFAULT_BURST;
	private int reallocSize = DEFAULT_BURST;
	
	public InputStreamToByteArray(InputStream source) {
		this(() -> source);
	}

	public InputStreamToByteArray(Supplier<InputStream> source) {
		this.source = source;
	}

	@Override
	public Byte[] perform(List<Closeable> resourceBin) throws IOException {
		InputStream source = this.source.get();
		resourceBin.add(source);
		ReadBuffer sourceBuffer = new ReadBuffer(new byte[DEFAULT_PREALLOC]);
		while (sourceBuffer.ensureEnoughAlloc(burstSize, reallocSize) != null) && sourceBuffer.readBurst(burstSize)) { }
		sourceBuffer.truncate();
		return sourceBuffer.sourceBytes;
	}

	public InputStreamToByteArray readBurstAt(int burstSize) {
		this.burstSize = burstSize;
		return this;
	}

	public InputStreamToByteArray reallocBy(int reallocSize) {
		this.reallocSize = reallocSize;
		return this;
	}
	
	protected static class ReadBuffer
	{
		public byte[] sourceBytes;
		public int sourceCursor;
		
		public ReadBuffer(byte[] sourceBytes) {
			this.sourceBytes = sourceBytes;
			this.sourceCursor = 0;
		}

		public boolean readBurst(InputStream source, int burstSize) throws IOException {
			int readLen = source.read(sourceBytes, sourceCursor, burstSize);
			if (readLen > 0) sourceCursor += readLen;
			return (readLen >= 0);
		}

		public boolean ensureEnoughAlloc(int burstSize, int reallocSize) {
			int remainingRoomInSource = (sourceBytes.length - sourceCursor);
			if (remainingRoomInSource < burstSize) realloc(reallocSize);
			return true;
		}

		public void realloc(int reallocSize)
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
		public void truncate() {
			if (sourceCursor != sourceBytes.length) {
				byte[] existingGen = sourceBytes;
				sourceBytes = new byte[sourceCursor];
				System.arraycopy(existingGen, 0, sourceBytes, 0, sourceCursor);
			}
		}
	}
}