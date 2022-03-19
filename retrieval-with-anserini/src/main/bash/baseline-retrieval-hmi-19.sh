#!/usr/bin/bash -e

REPO="/mnt/ceph/storage/data-tmp/latest/kibi9872/romcir22-keyqueries/"
TOPICS="${REPO}/third-party/health-misinfo-19/topics.xml"
INDEX="/home/webis/lucene-index.cw12b13.docvectors"
OUT_PREFIX="/mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/runs/hmi-19/run.hmi19"

java \
  -cp ${REPO}retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.search.SearchCollection \
  -index ${INDEX} \
  -topics ${TOPICS} -topicreader HmiXml \
  -bm25.k1 0.7 0.8 0.9 1.0 1.1 -bm25.b 0.3 0.35 0.4 0.45 0.5 \
  -bm25 \
  -threads 44 \
  -output ${OUT_PREFIX}.bm25 |tee bm25.logs


java \
  -cp ${REPO}retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.search.SearchCollection \
  -index ${INDEX} \
  -topics ${TOPICS} -topicreader HmiXml \
  -qld.mu 800 900 1000 1100 1200 1300 1400 1500 1600 1700 1800 1900 2000 2100 2200 2300 \
  -qld \
  -threads 44 \
  -output ${OUT_PREFIX}.qld |tee qld.logs


java \
  -cp ${REPO}retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.search.SearchCollection \
  -index ${INDEX} \
  -topics ${TOPICS} -topicreader HmiXml \
  -bm25.k1 0.7 0.8 0.9 1.0 1.1 -bm25.b 0.3 0.35 0.4 0.45 0.5 \
  -rm3.fbTerms 9 10 11 -rm3.fbDocs 9 10 11 -rm3.originalQueryWeight 0.4 0.5 0.6 \
  -bm25 -rm3 \
  -threads 44 \
  -output ${OUT_PREFIX}.bm25+rm3 |tee bm25+rm3.logs


java \
  -cp ${REPO}retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.search.SearchCollection \
  -index ${INDEX} \
  -topics ${TOPICS} -topicreader HmiXml \
  -qld -rm3 \
  -qld.mu 800 900 1000 1100 1200 1300 1400 1500 1600 1700 1800 1900 2000 2100 2200 2300 \
  -rm3.fbTerms 9 10 11 -rm3.fbDocs 9 10 11 -rm3.originalQueryWeight 0.4 0.5 0.6 \
  -threads 44 \
  -output ${OUT_PREFIX}.qld+rm3 |tee qld+rm3.logs

