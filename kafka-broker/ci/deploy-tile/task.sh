#!/bin/sh -ex


TILE_FILE=`cd broker-tile; ls *.pivotal`
if [ -z "${TILE_FILE}" ]; then
	echo "No files matching broker-tile/*.pivotal"
	ls -lR broker-tile
	exit 1
fi

PRODUCT=`echo "${TILE_FILE}" | sed "s/-[^-]*$//"`
VERSION=`more version/number`

cd pcf-environment

echo "Available products:"
pcf products
echo

echo "Uploading ${TILE_FILE}"
pcf import ../broker-tile/${TILE_FILE}
echo

echo "Available products:"
pcf products
pcf is-available "${PRODUCT}" "${VERSION}"
echo

echo "Installing product ${PRODUCT} version ${VERSION}"
pcf install "${PRODUCT}" "${VERSION}"
echo

echo "Available products:"
pcf products
pcf is-installed "${PRODUCT}" "${VERSION}"
echo

echo "Configuring product ${PRODUCT}"
pcf configure "${PRODUCT}" "../tile-repo/kafka-broker/ci/missing-properties.yml"
echo

echo "Applying Changes"
pcf apply-changes
echo
