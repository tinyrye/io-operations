package com.softwhistle.io;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileResources
{
	public static ResourceOpener<InputStream> inputStreamSupplier(File file) {
		return () -> new FileInputStream(file);
	}

	public static ResourceOpener<OutputStream> outputStreamSupplier(File file) {
		return () -> new FileOutputStream(file);
	}
}