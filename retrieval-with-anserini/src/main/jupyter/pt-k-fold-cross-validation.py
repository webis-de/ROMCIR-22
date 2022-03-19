#!/usr/bin/env python3

import pyterrier as pt
from glob import glob
from tqdm import tqdm
import pandas as pd
from trectools import TrecQrel
from pathlib import Path
import json
import argparse

RUN_DIR='/mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery/runs/'

if not pt.started():
    pt.init()
    
def to_pt_qrels(ret):
    ret = ret.qrels_data.copy()
    del ret['q0']
    ret = ret.rename(columns={'query': 'qid','docid': 'docno', 'rel': 'label'})
    ret['qid'] = ret['qid'].astype(str)
    ret['label'] = ret['label'].astype(int)
    return ret

QRELS = {
    19: to_pt_qrels(TrecQrel('../../../../third-party/health-misinfo-19/misinfo-qrels-graded.helpful-only')),
    20: to_pt_qrels(TrecQrel('../../../../third-party/health-misinfo-20/misinfo-qrels-graded.helpful-only')),
    21: to_pt_qrels(TrecQrel('../../../../third-party/health-misinfo-21/misinfo-qrels-graded.helpful-only')),
}

def dummy_topics(year):
    ret = []
    for qid in QRELS[year]['qid'].astype(str).unique():
        ret += [{
            'qid': qid,
            'query': 'dummy query ' + qid,
        }]
        
    return pd.DataFrame(ret)

TOPICS = {
    19: dummy_topics(19),
    20: dummy_topics(20),
    21: dummy_topics(21),
}

FOLDS = {
    19: [[1, 4, 5, 6, 8, 9, 10, 11, 12, 13, 15, 16, 19], [20, 21, 23, 25, 26, 27, 28, 29, 31, 34, 36, 37, 38], [39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51]],
    20: [[1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16], [17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31], [32, 34, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 47, 49, 50]],
    21: [[101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112], [114, 115, 117, 118, 120, 121, 122, 127, 128, 129, 131, 132], [133, 134, 136, 137, 139, 140, 143, 144, 145, 146, 149]],    
}

def folds(year):
    ret = []
    for i in range(0,3):
        fold = FOLDS[year][i]
        i = dummy_topics(year)
        i = i[i.qid.astype(int).isin(fold)]
        ret += [i]
    return ret

class RunFileTransformer(pt.transformer.TransformerBase):
    def __init__(self, *args, **kwargs):
        super(RunFileTransformer, self).__init__(*args, **kwargs)
        self.base_dir = None
        self.approach = None

    def transform(self, r):
        qids = set(r['qid'].astype(str).unique())
        ret = self.load_approach()
        ret = ret[ret['qid'].astype(str).isin(qids)]
        
        return ret

    def load_approach(self):
        assert self.approach is not None
        assert self.base_dir is not None
        
        return pt.io.read_results(self.base_dir + '/' + self.approach)

def to_config(t):
    ret = {}
    for i in t:
        ret[i[1]] = i[2]
    return ret

def main(year, base_dir, rel=None):
    out_dir = str(year) + '/' + base_dir + '/'
    if rel:
        out_dir = str(year) + '/' + base_dir + 'rel-' + str(rel) + '/'

    Path(out_dir).mkdir(parents=True, exist_ok=True)
    base_dir = RUN_DIR + base_dir
    approaches = [i.split(base_dir + '/')[1] for i in glob(base_dir + '/*')]
    if rel:
        approaches = [i for i in approaches if (';rel=' + str(rel) + ')') in i]
    d = RunFileTransformer()
    
    a, b = pt.KFoldGridSearch(
        d,
        {d: {'approach': approaches, 'base_dir': [base_dir]}},
        folds(year),
        QRELS[year],
        'ndcg_cut.10',
    )

    b = [to_config(i) for i in b]
    pt.io.write_results(a, out_dir + 'run.txt')
    json.dump(b, open(out_dir + 'params.json', 'w'))
    
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--year', type=int, required=True)
    parser.add_argument('--base_dir', type=str, required=True)

    args = parser.parse_args()
    
    main(args.year, args.base_dir)
    for i in range(1,6):
        main(args.year, args.base_dir, i)

