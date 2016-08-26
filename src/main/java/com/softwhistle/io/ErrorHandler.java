package com.softwhistle.io;

import java.io.IOException;

public interface ErrorHandler
{
	public static class Basic implements ErrorHandler {

	}

	public default boolean ignoreOnOpen(IOException ex) {
		return false;
	}

	public default boolean ignoreFromOperation(IOException ex) {
		return false;
	}
	
	public default boolean ignoreOnClose(IOException ex) {
		return false;
	}
}