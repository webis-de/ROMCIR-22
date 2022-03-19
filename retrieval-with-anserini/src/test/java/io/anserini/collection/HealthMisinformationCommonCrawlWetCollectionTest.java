package io.anserini.collection;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class HealthMisinformationCommonCrawlWetCollectionTest {
	@Test
	public void approveDocumentsInSample() {
		HealthMisinformationCommonCrawlWetCollection col = new HealthMisinformationCommonCrawlWetCollection(Paths.get("src/test/resources"));
		List<String> docIds = new ArrayList<>();
		
		col.forEach(i -> {
			i.forEach(doc -> {
				if(doc.indexable()) {
					docIds.add(doc.id());
				}
			});
		});
		
		Assert.assertEquals("[2c90de37-c1f0-430b-9867-87f14e465dd8, 5b3dab17-8930-4e81-a060-f5f3f39455c6, 86be6a68-aad7-4c62-bea8-2a2bfa1b12dd, bb968381-3aeb-4883-9c18-92ea36123972, 9184035b-b71f-4aa4-b6d7-ec9fa3b20804]", docIds.stream().limit(5).collect(Collectors.toList()).toString());
	}
}
