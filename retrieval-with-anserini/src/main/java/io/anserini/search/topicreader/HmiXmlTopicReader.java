package io.anserini.search.topicreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

import lombok.SneakyThrows;

/**
 * Topic reader for standard XML format used in the TREC Health-Misinformation (HMI) Tracks.
 */
public class HmiXmlTopicReader extends TopicReader<Integer> {
  public HmiXmlTopicReader(Path topicFile) {
    super(topicFile);
  }

  @SneakyThrows
  public static void main(String[] args) {
    for(int year: new int[] {19, 20, 21}) {
      HmiXmlTopicReader topics = new HmiXmlTopicReader(Paths.get("../third-party/health-misinfo-" + year + "/topics.xml"));
      String out = "";
      for(Map.Entry<Integer, Map<String, String>> topic: topics.read().entrySet()) {
        Map<String, Object> tmp = new LinkedHashMap<>();
        tmp.put("topic", topic.getKey());
        tmp.put("title", topic.getValue().get("title"));
        
        out += new ObjectMapper().writeValueAsString(tmp) + "\n"; 
      }
      Files.write(out.strip().getBytes(), Paths.get("../third-party/health-misinfo-" + year + "/topics.jsonl").toFile());
    }
  }
  
  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    Map<String,String> fields = new HashMap<>();

    String number = "";
    String query = "";

    String line;

    while ((line = bRdr.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("<number")) {
        number = StringUtils.substringBetween(line, "<number>", "</number>").trim();
      }
      if (line.startsWith("<query>") && line.endsWith("</query>")) {
        query = line.substring(7, line.length() - 8).trim();
        fields.put("title", query);
      }
      if (line.startsWith("<title>") && line.endsWith("</title>")) {
        query = StringUtils.substringBetween(line, "<title>", "</title>").trim();
        fields.put("title", query);
      }
      if (line.startsWith("</topic>")) {
        map.put(Integer.valueOf(number), fields);
        fields = new HashMap<>();
      }
    }

    return map;
  }
}