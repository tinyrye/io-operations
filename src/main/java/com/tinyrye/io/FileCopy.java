package com.tinyrye.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopy extends Operation
{
	private ResourceLoader<File> sourceHold, destinationHold;
	private boolean autoCreateDestination = false;
	private StreamCopy copyOp;
	
	public FileCopy(File source, File destination) {
		this(new ResourceLoader<File>().set(source), new ResourceLoader<File>().set(destination));
	}

	public FileCopy(ResourceLoader<File> sourceHold, ResourceLoader<File> destinationHold) {
		this.sourceHold = sourceHold;
		this.destinationHold = destinationHold;
	}
	
	public FileCopy autoCreateDestination() {
		autoCreateDestination = true;
		return this;
	}
	
	@Override
	protected void performOperation() throws IOException {
		File source = sourceHold.get();
		File destination = destinationHold.get();
		if (autoCreateDestination) ensureFile(destination);
		copyOp = new StreamCopy(new FileInputStream(source), new FileOutputStream(destination));
		copyOp.performOperation();
	}
	
	@Override
	public void close() throws IOException {
		if (copyOp != null) { copyOp.close(); copyOp = null; }
	}
}