package io.anserini.search.topicreader;

import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Assert;
import org.junit.Test;

import lombok.SneakyThrows;

public class HmiXmlTopicReaderTest {
	@Test
	@SneakyThrows
	public void approveTwoTopicsFromHmi19() {
		SortedMap<Integer, Map<String, String>> actual = new HmiXmlTopicReader(Paths.get("../third-party/health-misinfo-19/topics.xml")).read();
		Assert.assertEquals(51, actual.size());
		

		Assert.assertEquals("dehumidifiers asthma", actual.get(51).get("title"));
		Assert.assertEquals("probiotics eczema", actual.get(42).get("title"));
		Assert.assertEquals("cranberries urinary tract infections", actual.get(1).get("title"));
	}
	
	@Test
	@SneakyThrows
	public void approveTwoTopicsFromHmi20() {
		SortedMap<Integer, Map<String, String>> actual = new HmiXmlTopicReader(Paths.get("../third-party/health-misinfo-20/topics.xml")).read();
		Assert.assertEquals(50, actual.size());
		
		Assert.assertEquals("Lemon water COVID-19", actual.get(50).get("title"));
		Assert.assertEquals("Inhalers COVID-19", actual.get(42).get("title"));
		Assert.assertEquals("Vitamin D COVID-19", actual.get(1).get("title"));
	}
	
	@Test
	@SneakyThrows
	public void approveTwoTopicsFromHmi21() {
		SortedMap<Integer, Map<String, String>> actual = new HmiXmlTopicReader(Paths.get("../third-party/health-misinfo-21/topics.xml")).read();
		Assert.assertEquals(50, actual.size());
		
		Assert.assertEquals("ankle brace achilles tendonitis", actual.get(101).get("title"));
		Assert.assertEquals("hip osteoarthritis at-home exercises", actual.get(149).get("title"));
		Assert.assertEquals("antioxidant supplements fertility", actual.get(150).get("title"));
	}
}
