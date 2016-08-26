package com.softwhistle.io;

@FunctionalInterface
public interface AlternativeErrorHandler<T> extends ErrorHandler {
	T alternative();
}