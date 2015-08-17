package com.tinyrye.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamToString extends Operation
{
	private ResourceLoader<InputStream> source;
	private String sourceString;
	
	public InputStreamToString(InputStream source) {
		this(new ResourceLoader<InputStream>().set(source));
	}

	public InputStreamToString(ResourceLoader<InputStream> source) {
		this.source = source;
	}

	public String runToString() {
		run();
		return toString();
	}
	
	@Override
	protected void performOperation() throws IOException
	{
		final InputStreamReader streamReader = new InputStreamReader(source.get());
		final StringBuffer accumBuf = new StringBuffer();
		final char[] perReadBuf = new char[1024];
		int readLen;
		while ((readLen = streamReader.read(perReadBuf, 0, 1024)) != -1) {
			if (readLen > 0) {
				accumBuf.append(perReadBuf, 0, readLen);
			}
		}
		sourceString = accumBuf.toString();
	}
	
	@Override
	public void close() throws IOException {
		if (source != null) {
			source.get().close();
			source = null;
		}
	}
	
	@Override
	public String toString() {
		return sourceString;
	}
}