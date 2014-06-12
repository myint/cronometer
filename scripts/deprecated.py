#!/usr/bin/env python

"""List deprecated USDA foods."""

import argparse
import sys


def read_index(filename):
    result = {}

    with open(filename) as input_file:
        for line in input_file.readlines():
            (key, value) = line.split('|')
            result[key] = value.strip()

    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('old', help='old food index')
    parser.add_argument('new', help='new food index')
    args = parser.parse_args()

    old = read_index(args.old)
    new = read_index(args.new)

    for key in set(old) - set(new):
        print('{}|{}'.format(key, old[key]))


if __name__ == '__main__':
    sys.exit(main())
