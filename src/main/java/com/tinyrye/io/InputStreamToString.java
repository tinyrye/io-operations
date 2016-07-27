package com.tinyrye.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

public class InputStreamToString implements Operation
{
	private Supplier<InputStream> source;
	private String charset;
	private String sourceString;
	
	public InputStreamToString(InputStream source) {
		this(() -> source);
	}

	public InputStreamToString(Supplier<InputStream> source) {
		this.source = source;
	}

	@Override
	public List<Closeable> perform() throws IOException {
		InputStreamToByteArray transform = new InputStreamToByteArray(source);
		try { return transform.perform(); }
		finally { sourceString = new String(transform.getBytes()); }
	}

	public String runToString() {
		run();
		return toString();
	}
	
	@Override
	public String toString() {
		return sourceString;
	}
}