#!/bin/sh -ex

cd tile-repo/kafka-broker

mkdir target
cp ../../broker-jar/*.jar target/kafka-broker.jar

ver=`more ../../version/number`
tile build ${ver}

file=`ls product/*.pivotal`
filename=$(basename "${file}")
filename="${filename%-*}"

cp ${file} ../../broker-tile/${filename}-${ver}.pivotal
cp tile-history.yml ../../tile-history-new/tile-history-${ver}.yml