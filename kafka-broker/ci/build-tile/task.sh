#!/bin/sh -ex

cd tile-repo/kafka-broker

mkdir target
cp ../../broker-jar/*.jar target/kafka-broker.jar
tile build

file=`ls product/*.pivotal`
filename=$(basename "${file}")
filename="${filename%-*}"
ver=`more ../../version/number`

cp ${file} ../../broker-tile/${filename}-${ver}.pivotal
cp tile-history.yml ../../tile-history-new/tile-history.yml