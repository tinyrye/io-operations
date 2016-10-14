package com.softwhistle.io;

import java.io.IOException;

@FunctionalInterface
public interface ErrorHandler
{
	/**
	 * @return whether the exception was handled and can be suppressed from
	 * further processing.
	 */
	boolean handle(IOException ex, Phase phase);
}