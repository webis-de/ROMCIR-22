#!/usr/bin/env python3
import datetime
import sys
from statistics import median

def load_max_time(file_name):
    with open(file_name, 'r') as f:
        ret = []
        for l in f:
            if 'topics processed in ' in l:
                l = l.split('topics processed in ')[1].strip()
                l = l.split(':')
                assert len(l) == 3
                l = (int(l[0])*60*60) + (int(l[1]) *60) + int(l[2])
                ret += [l]

        return max(ret)

def load_median_time(file_name):
    with open(file_name, 'r') as f:
        ret = []
        for l in f:
            if 'topics processed in ' in l:
                l = l.split('topics processed in ')[1].strip()
                l = l.split(':')
                assert len(l) == 3
                l = (int(l[0])*60*60) + (int(l[1]) *60) + int(l[2])
                ret += [l]

        return median(ret)

if __name__ == '__main__':
    print(sys.argv[1])

    t = load_max_time(sys.argv[1])
    print('Overall (max): ' + str(datetime.timedelta(seconds=t)))
    print('Per Query (max): ' + str(datetime.timedelta(seconds=t/50)))

    t = load_median_time(sys.argv[1])
    print('Overall (median): ' + str(datetime.timedelta(seconds=t)))
    print('Per Query (median): ' + str(datetime.timedelta(seconds=t/50)))

