package de.webis.romcir22.index_construction;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.anserini.collection.DocumentCollection;
import io.anserini.collection.SourceDocument;
import lombok.Data;
import lombok.SneakyThrows;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

@Data
public class ExportAllDocumentIdsArguments<T extends SourceDocument> {
	private final int numThreads;
	private final DocumentCollection<T> collection;
	private final String output;
	
	public static <T extends SourceDocument> ExportAllDocumentIdsArguments<T> parseArgs(String...args) {
		return parseArgs(parse(args));
	}
	
	@SneakyThrows
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends SourceDocument> ExportAllDocumentIdsArguments<T> parseArgs(Namespace args) {
		if(args == null) {
			return null;
		}

		Class<?> collectionClass = Class.forName("io.anserini.collection." + args.getString("collection"));
	    
		return new ExportAllDocumentIdsArguments<>(
			args.getInt("threads"),
			(DocumentCollection)collectionClass.getConstructor(Path.class).newInstance(Paths.get(args.getString("input"))),
			args.getString("output")
		);
	}
	
	private static Namespace parse(String[] args) {
		ArgumentParser parser = argParser();
		
		try {
			return parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			return null;
		}
	}

	static ArgumentParser argParser() {
		ArgumentParser ret = ArgumentParsers.newFor("ExportAllDocumentsIds: Process some ir-collection and extract all document ids.")
				.build();
		
		ret.addArgument("-threads")
		.help("Number of processing threads.")
			.type(Integer.class);
		
		ret.addArgument("-collection")
			.help("Collection class in package 'io.anserini.collection'.")
			.type(String.class);

		ret.addArgument("-input")
			.help("Location of input collection.")
			.type(String.class);
		
		ret.addArgument("-output")
			.help("Location where the result is stored (each id on a line)")
			.type(String.class)
			.required(true);
		
		return ret;
	}
}
