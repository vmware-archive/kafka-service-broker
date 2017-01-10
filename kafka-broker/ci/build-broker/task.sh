#!/bin/sh -ex

cd tile-repo
mvn -e install

cd kafka-broker

file=`ls target/*.jar`
filename=$(basename "${file}")
filename=${filename%.*}
ver=`more ../../version/number`

cp ${file} ../../broker-jar/${filename}-${ver}.jar