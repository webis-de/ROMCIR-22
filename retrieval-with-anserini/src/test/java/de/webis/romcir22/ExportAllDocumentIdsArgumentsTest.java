package de.webis.romcir22;

import org.junit.Assert;
import org.junit.Test;

import de.webis.romcir22.index_construction.ExportAllDocumentIdsArguments;

public class ExportAllDocumentIdsArgumentsTest {
	@Test
	public void testValidArgsForC4() {
		ExportAllDocumentIdsArguments<?> args = ExportAllDocumentIdsArguments.parseArgs(
			"-threads", "2", "-collection", "C4NoCleanCollection", "-output", "a", "-input", "."
		);
		
		Assert.assertEquals(2, args.getNumThreads());
		Assert.assertEquals("class io.anserini.collection.C4NoCleanCollection", args.getCollection().getClass().toString());
	}
	
	@Test
	public void testValidArgsForBibtex() {
		ExportAllDocumentIdsArguments<?> args = ExportAllDocumentIdsArguments.parseArgs(
			"-threads", "4", "-collection", "BibtexCollection", "-output", "a", "-input", "."
		);
		
		Assert.assertEquals(4, args.getNumThreads());
		Assert.assertEquals("class io.anserini.collection.BibtexCollection", args.getCollection().getClass().toString());
	}
}
