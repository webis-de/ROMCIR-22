#!/usr/bin/env python3
from trectools import TrecRun, TrecQrel
from glob import glob
from tqdm import tqdm
import sys
import gzip

DIR = '../../../../third-party/health-misinfo-'

def trec_run(file_name):
    if file_name.endswith('s.gz'):
        return TrecRun(gzip.open(file_name)).run_data.docid.unique()
    else:
        return 

def all_runs(year):
    ret = set()
    for f in tqdm(glob(DIR + str(year) +'/runs/*')):
        ret.update(TrecRun(f).run_data.docid.unique())

    return ret

def all_qrels(year):
    ret = set()
    qrels = None
    if year == 19:
        qrels = TrecQrel(DIR + str(year) +'/2019qrels_relevance.txt').qrels_data
    elif year == 20:
        qrels = TrecQrel(DIR + str(year) + '/qrels/misinfo-2020-qrels-del-me').qrels_data
    elif year == 21:
        qrels = TrecQrel(DIR + str(year) + '/qrels/qrels-35topics-del-me.txt').qrels_data

    return set(qrels.docid.unique())

if __name__ == '__main__':
    for year in [19, 20, 21]:
        ids = set(list(all_qrels(year)) + list(all_runs(year)))
        ids = list(ids)
        ids = sorted(ids)
        with open(DIR + str(year) + '/important-docs-to-include.txt', 'w') as f:
            for docid in ids:
                f.write(docid + '\n')

