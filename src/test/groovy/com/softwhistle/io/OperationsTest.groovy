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

	def "openAndTransform: resource opener with function"() {
		when:
			def fileContents = Operations.openAndTransform(testFileStream, { fs ->
				IOUtils.toString(fs)
			} as ResourceFunction<InputStream,String>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "transform: resource with function"() {
		when:
			def fileContents = Operations.transform(testFileStream.open(), { fs ->
				IOUtils.toString(fs)
			} as ResourceFunction<InputStream,String>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "openAndTransform: resource opener exception"() {
		when:
			Operations.openAndTransform(testFileThatDoesNotExistStream, null as ResourceFunction<InputStream,String>)
		then:
			thrown(RuntimeException)
	}

	def "openAndTransform: resource opener exception handled with alternative"() {
		setup:
			def errorHandler = Mock(ErrorHandler)
			1 * errorHandler.handle(_, _) >> true
			def alternativeSupplier = Mock(Supplier)
			1 * alternativeSupplier.get() >> 'No one expects the Spanish Inquisition!'
		when:
			def result = Operations.openAndTransform(testFileThatDoesNotExistStream, null as ResourceFunction<InputStream,String>, errorHandler, alternativeSupplier)
		then:
			notThrown(RuntimeException)
			result == 'No one expects the Spanish Inquisition!'
	}

	def "openAndTransform: resource opener exception handled without alternative"() {
		setup:
			def errorHandler = Mock(ErrorHandler)
			1 * errorHandler.handle(_, _) >> true
		when:
			Operations.openAndTransform(testFileThatDoesNotExistStream, null as ResourceFunction<InputStream,String>, errorHandler, null)
		then:
			thrown(NoSuchElementException)
	}

	def "openAndRun: resource opener with acceptor"() {
		when:
			String fileContents
			Operations.openAndRun(testFileStream, { fs ->
				fileContents = IOUtils.toString(fs)
			} as ResourceConsumer<InputStream>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "run: resource with acceptor"() {
		when:
			String fileContents
			Operations.run(testFileStream.open(), { fs ->
				fileContents = IOUtils.toString(fs)
			} as ResourceConsumer<InputStream>)
		then:
			fileContents == PETER_BISHOP_WORDS_OF_WISDOM
	}

	def "run: resource operation exception"() {
		when:
			Operations.run(testFileStream.open(), { str -> throw new IOException("Whoops") })
		then:
			thrown(RuntimeException)
	}
	
	def "run: resource operation exception handled"() {
		setup:
			def errorHandler = Mock(ErrorHandler)
			errorHandler.handle(_, _) >> true
		when:
			Operations.run(testFileStream.open(), { str -> throw new IOException("Whoops") }, errorHandler)
		then:
			notThrown(RuntimeException)
	}

	def "openAndRun: resource opener exception"() {
		when:
			Operations.openAndRun(testFileThatDoesNotExistStream, null as ResourceFunction<InputStream,String>)
		then:
			thrown(RuntimeException)
	}
}