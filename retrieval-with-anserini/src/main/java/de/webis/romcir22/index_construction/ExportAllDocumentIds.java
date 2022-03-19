package de.webis.romcir22.index_construction;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import lombok.Data;
import lombok.SneakyThrows;

public class ExportAllDocumentIds {

	private static final Logger LOG = LogManager.getLogger(ExportAllDocumentIds.class);

	private static List<String> ALL_IDS;

	@SneakyThrows
	public static void main(String[] args) {
		ExportAllDocumentIdsArguments<?> parsedArgs = ExportAllDocumentIdsArguments.parseArgs(args);

		if (parsedArgs == null) {
			return;
		}

		ALL_IDS = Collections.synchronizedList(new ArrayList<String>());
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(parsedArgs.getNumThreads());
		List<?> segmentPaths = parsedArgs.getCollection().getSegmentPaths();

		for (int i = 0; i < segmentPaths.size(); i++) {
			executor.execute(new ExportDocumentIdsThread(parsedArgs.getCollection(), (Path) segmentPaths.get(i), 60000000));
		}
		
		executor.shutdown();
		while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
			LOG.info(String.format("%.2f%% of files completed, %,d documents indexed",
					(double) executor.getCompletedTaskCount() / segmentPaths.size() * 100.0d, ALL_IDS.size()));
		}

		try(FileWriter writer = new FileWriter(new File(parsedArgs.getOutput()))) {
			for(String id: ALL_IDS) {
				writer.write(id + "\n");
			}
		}
	}

	@Data
	private static class ExportDocumentIdsThread extends Thread {
		private final DocumentCollection<?> collection;
		private final Path inputFile;
		private final int maxSize;
		
		private static final int BATCH_SIZE = 25000;

		@Override
		@SneakyThrows
		public void run() {
			try {
				if(ALL_IDS.size() > maxSize) {
					return;
				}
				
				FileSegment<?> segment = collection.createFileSegment(inputFile);
	
				List<String> batch = new ArrayList<>(2*BATCH_SIZE);
				int batchCnt = 0;
				for (SourceDocument d : segment) {
					if (!d.indexable()) {
						continue;
					}
					String id = d.id();
					
					if(id != null && !id.trim().isEmpty()) {
						batch.add(id.trim());
						batchCnt++;
					}
					
					
					if(batchCnt%BATCH_SIZE == 0) {
						batchCnt = 0;
						ALL_IDS.addAll(batch);
						batch = new ArrayList<>(2*BATCH_SIZE);
						
						if(ALL_IDS.size() > maxSize) {
							break;
						}
					}
				}
				
				ALL_IDS.addAll(batch);
			} catch(Exception e) {}
		}
	}
}
