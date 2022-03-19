# Health-Misinfo-2019

Use `epsilonweb011` for retrieval-experiments. The anserini-index lies under ´/home/webis/lucene-index.cw12b13.docvectors`.

# Create 2021 Index

```
mvn clean install

java -cp target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar de.webis.romcir22.index_construction.ExportAllDocumentIds \
    -collection C4NoCleanCollection \
    -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpora-trec/trec-health-misinformation-c4/c4/en.noclean/ \
    -threads 44\
    -output c4-ids


java -cp /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.index.IndexCollection \
    -collection C4NoCleanCollection \
    -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpora-trec/trec-health-misinformation-c4/c4/en.noclean/ \
    -index indexes/lucene-index.c4noclean \
    -generator DefaultLuceneDocumentGenerator \
    -whitelist /mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/ids-in-c4.csv \
    -threads 44 -storePositions -storeDocvectors -storeRaw |tee indexes-c4noclean.logs

java \
        -cp /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.index.IndexCollection \
        -collection HealthMisinformationCommonCrawlWetCollection \
        -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpora-trec/trec-health-misinformation-cc-news-20/ \
        -index indexes/lucene-index.cc-news-20 \
        -generator DefaultLuceneDocumentGenerator \
	-whitelist /mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/ids-cc20-news.csv \
        -threads 80 -storePositions -storeDocvectors |tee indexes-cc-news-20.logs
```

# Construction of small indexes with only judged documents for fast random document access

CW12 (used in HMI 19):
```
java \
        -cp /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.index.IndexCollection \
        -collection ClueWeb12Collection \
        -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpus-clueweb12/parts/ \
        -index indexes/lucene-index.cw12-judged-only-raw \
        -generator DefaultLuceneDocumentGenerator \
	-whitelist /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/third-party/health-misinfo-19/important-docs-to-include.txt \
        -threads 80 -storePositions -storeRaw -storeContents -storeDocvectors |tee indexes-cw12.logs
```


CC-NEWS (used in HMI 20):
```
java \
        -cp /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.index.IndexCollection \
        -collection HealthMisinformationCommonCrawlWetCollection \
        -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpora-trec/trec-health-misinformation-cc-news-20/ \
        -index indexes/lucene-index.cc-news-20-judged-only-raw \
        -generator DefaultLuceneDocumentGenerator \
	-whitelist /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/third-party/health-misinfo-20/important-docs-to-include.txt \
        -threads 80 -storePositions -storeRaw -storeContents -storeDocvectors
```

C4-NoClean (used in HMI 21):
```
java -cp /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/retrieval-with-anserini/target/romcir22-1.0-SNAPSHOT-jar-with-dependencies.jar io.anserini.index.IndexCollection \
    -collection C4NoCleanCollection \
    -input /mnt/ceph/storage/corpora/corpora-thirdparty/corpora-trec/trec-health-misinformation-c4/c4/en.noclean/ \
    -index indexes/lucene-index.c4noclean \
    -generator DefaultLuceneDocumentGenerator \
    -whitelist /mnt/ceph/storage/data-tmp/2021/kibi9872/romcir22-keyqueries/third-party/health-misinfo-21/important-docs-to-include.txt \
    -threads 80 -storePositions -storeDocvectors -storeRaw -storeContents |tee indexes-c4noclean.logs
```

- Indexing is running on gammaweb04, index is on `/home/webis/tmp/indexes/lucene-index.c4noclean`
- lucene-index.c4noclean is on epsilonweb013
- lucene-index.cc-news-20 is on epsilonweb014
- lucene-index.cw12b13.docvectors/ is on epsilonweb011

