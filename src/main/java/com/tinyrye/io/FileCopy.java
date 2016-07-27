package com.tinyrye.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class FileCopy extends StreamCopy
{
	private boolean autoCreateDestination = false;
	private final Supplier<File> destination;
	
	public FileCopy(File source, File destination) {
		this(() -> source, () -> destination);
	}

	public FileCopy(Supplier<File> source, Supplier<File> destination) {
		super(() -> new FileInputStream(source.get()), () -> new FileOutputStream(destination));
		this.destination = destination;
	}

	@Override
	public void perform(List<Closeable> resourceBin) throws IOException {
		File destination = this.destination.get();
		if (autoCreateDestination) PathPrecepts.ensureFile(destination);
		else if (! destination.exists() || ! destination.isFile()) throw new FileNotFoundException(destination.getAbsolutePath());
		super.perform(resourceBin);
	}
	
	public FileCopy autoCreateDestination() {
		autoCreateDestination = true;
		return this;
	}
}