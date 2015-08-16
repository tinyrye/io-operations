package com.tinyrye.io;

import java.io.IOException;

public interface ErrorHandler {
	void onPerform(IOException ex, Operation op);
	void onClose(IOException ex, Operation op);
}