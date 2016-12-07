#!/bin/sh -e

set -e

# echo "### Skipping remove"
# exit 0

TILE_FILE=`cd broker-tile; ls *.pivotal`
if [ -z "${TILE_FILE}" ]; then
	echo "No files matching broker-tile/*.pivotal"
	ls -lR broker-tile
	exit 1
fi

PRODUCT=`echo "${TILE_FILE}" | sed "s/-[^-]*$//"`

cd pcf-environment

echo "Available products:"
pcf products
echo

if ! pcf is-installed "${PRODUCT}" ; then
	echo "${PRODUCT} not installed - skipping removal"
	exit 0
fi

echo "Uninstalling ${PRODUCT}"
pcf uninstall "${PRODUCT}"
echo

echo "Applying Changes"
pcf apply-changes
echo

echo "Available products:"
pcf products
echo

if pcf is-installed "${PRODUCT}" ; then
	echo "${PRODUCT} remains installed - remove failed"
	exit 1
fi