package com.tinyrye.io;

import java.io.*;

/**
 * Essentially a wrapping over an IO function in order  call the operation within
 * a larger context such as a try-catch.
 */
public abstract class Operation implements Runnable, Closeable
{
	protected abstract void performOperation() throws IOException;
	
	private boolean performQuietly = false;
	private boolean keepOpen = false;
	private boolean closeQuietly = false;
	private ErrorHandler handler;

	/**
	 * Run operation and close resources (unless)
	 * IOExceptions are never thrown directly; instead they are
	 * 1) if handler defined then route error to handler.
	 * 2) else if quietly flag is not set then throw in wrapping RuntimeException,
	 * 3) else if quietly flag is set then swallow exception
	 */
	@Override
	public final void run()
	{
		try { performOperation(); }
		catch (IOException ex) {
			if (handler != null) handler.onPerform(ex, this);
			else if (! performQuietly) throw new RuntimeException(ex);
			// else -> ignore
		}
		if (! keepOpen)
		{
			try { close(); }
			catch (IOException ex) {
				if (handler != null) handler.onClose(ex, this);
				else if (! closeQuietly) throw new RuntimeException(ex);
				// else -> ignore
			}
		}
	}

	/**
	 * Optional behavior: exceptions while performing IO operation or closing
	 * resources are ignored; error handler being specified takes precedence
	 * over this flag .. ie. of error handler throws exception on perform or
	 * close then that exception is not ignored.
	 */
	public Operation runQuietly() {
		performQuietly();
		closeQuietly();
		return this;
	}
	
	/**
	 * Optional behavior: exceptions while performing IO operation are ignored;
	 * error handler being specified takes precedence over this flag .. ie. of
	 * error handler throws exception on perform then that exception is not
	 * ignored.
	 */
	public Operation performQuietly() {
		performQuietly = true;
		return this;
	}
	
	/**
	 * Optional behavior: exceptions while closing any streams or closeables are
	 * ignored; error handler being specified takes precedence over this flag ..
	 * ie. of error handler throws exception on close then that exception is not
	 * ignored.
	 */
	public Operation closeQuietly() {
		closeQuietly = true;
		return this;
	}

	/**
	 * Optional behavior: any streams or closeables are kept open after run.
	 */
	public Operation keepOpen() {
		keepOpen = true;
		return this;
	}

	/**
	 * Optional behavior: exception during perform or close handled with this.
	 */
	public Operation onException(ErrorHandler handler) {
		this.handler = handler;
		return this;
	}

	/**
	 * Error handler or execution chain examiner may want to identify
	 * this operation with a unique name.
	 */
	public String name() {
		return getClass().getName();
	}
	
	/**
	 * Helper method for implemention: ensure file and its parent
	 * directories exist. Make necessary nesting directories in
	 * addition to creating an empty file by the requested path.
	 */
	protected boolean ensureFile(File file) throws IOException
	{
		if (! file.exists())
		{
			if (ensureDirectories(file.getParentFile())) {
				return file.createNewFile();
			}
			else {
				return false;
			}
		}
		else return true;
	}

	/**
	 * Helper method for implemention: ensure directory exists; if parent
	 * directories do not exist however, then this will not succeed: this
	 * method will not create nesting directories.
	 */
	protected boolean ensureDirectory(File directory) throws IOException {
		if (! directory.exists()) return directory.mkdir();
		else return true;
	}

	/**
	 * Helper method for implemention: ensure directory and its parent
	 * directories exist. Make necessary nesting directories in addition
	 * to creating the leaf directory by the requested path.
	 */
	protected boolean ensureDirectories(File directory) throws IOException {
		if (! directory.exists()) return directory.mkdirs();
		else return true;
	}
}