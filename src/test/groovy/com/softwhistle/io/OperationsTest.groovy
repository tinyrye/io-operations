package com.softwhistle.io

import static org.junit.Assert.assertEquals

import java.util.function.Supplier

import spock.lang.*

import org.apache.commons.io.IOUtils

class Holder<T> {
	T value
}

class OperationsTest extends Specification
{
	static final String PETER_BISHOP_WORDS_OF_WISDOM = 'Na einai kalytera anthropo apo ton patera toy!'

	@Shared def testFileStream = {
		new FileInputStream(new File('src/test/resources/com/softwhistle/io/peterbishop.txt'))
	} as ResourceOpener<InputStream>

	@Shared def testFileThatDoesNotExistStream = {
		new FileInputStream(new File('src/test/resources/com/softwhistle/io/whodoesnumbertwoworkfor.json'))
	} as ResourceOpener<InputStream>

	def "openThenApply: resource opener with function"() {
		when:
			def fileContents = Operations.open(testFileStream).apply({ fs ->
				IOUtils.toString(fs)
			} as ResourceFunction<InputStream,String>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "openThenApply: resource opener exception"() {
		when:
			Operations.open(testFileThatDoesNotExistStream).apply(null as ResourceFunction<InputStream,String>)
		then:
			thrown(RuntimeException)
	}

	def "openThenApply: resource opener exception handled with alternative"() {
		setup:
			def errorHandler = Mock(ErrorHandler)
			1 * errorHandler.handle(_, _) >> true
			def alternativeSupplier = Mock(Supplier)
			1 * alternativeSupplier.get() >> 'No one expects the Spanish Inquisition!'
		when:
			def result = Operations.open(testFileThatDoesNotExistStream, errorHandler).applyOpt(null, alternativeSupplier)
		then:
			notThrown(RuntimeException)
			result == 'No one expects the Spanish Inquisition!'
	}

	def "openThenAccept: resource opener with acceptor"() {
		when:
			String fileContents
			Operations.open(testFileStream).accept({ fs ->
				fileContents = IOUtils.toString(fs)
			} as ResourceConsumer<InputStream>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "withThenAccept: resource opener no acceptor"() {
		when:
			Operations.open(testFileThatDoesNotExistStream).accept(null as ResourceFunction<InputStream,String>)
		then:
			thrown(RuntimeException)
	}

	def "withThenApply: resource operation exception"() {
		when:
			Operations.with(testFileStream.open()).apply({ str -> throw new IOException("Whoops") })
		then:
			thrown(RuntimeException)
	}

	def "withThenAccept: resource with acceptor"() {
		when:
			String fileContents
			Operations.with(testFileStream.open()).accept({ fs ->
				fileContents = IOUtils.toString(fs)
			} as ResourceConsumer<InputStream>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}
	
	def "withThenAccept: resource operation exception handled"() {
		setup:
			def errorHandler = Mock(ErrorHandler)
			errorHandler.handle(_, _) >> true
		when:
			Operations.with(testFileStream.open(), errorHandler).accept({ str -> throw new IOException("Whoops") })
		then:
			notThrown(RuntimeException)
	}
}