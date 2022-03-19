#!/usr/bin/bash -e

if [ -z "$YEAR" ]; then
  echo "Please pass a 'YEAR' environment variable."
  exit 1
fi

if [ -z "$INDEX" ]; then
  echo "Please pass a 'INDEX' environment variable."
  exit 1
fi

REPO="/mnt/ceph/storage/data-tmp/latest/kibi9872/romcir22-keyqueries/"
TOPICS="${REPO}/third-party/health-misinfo-${YEAR}/topics.xml"
OUT_DIR="/mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/runs/hmi-${YEAR}-move-to-top/"
OUT_PREFIX="${OUT_DIR}run.hmi${YEAR}"
QRELS="${REPO}/third-party/health-misinfo-${YEAR}/misinfo-qrels-graded.helpful-only"

mkdir -p ${OUT_DIR}

/mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/code-in-backup/thesis-huck-anserini/target/appassembler/bin/SearchCollection \
  -index ${INDEX} \
  -qrels ${QRELS} \
  -topics ${TOPICS} -topicreader HmiXml \
  -bm25 \
  -sequentialRelevanceFeedback \
  -srv.relDocs 1 2 3 4 5 \
  -rerankCutoff 1000 \
  -toTop \
  -threads 44 \
  -output ${OUT_PREFIX}.bm25 |tee bm25-move-to-top-${YEAR}.logs

