#!/usr/bin/env bash

currentTag=`git describe --tags`
buildVersion=`grep -m 1 -oP '^[\s]*<version>\K(.*)(?=(</version>[\s]*$))' pom.xml`

echo current tag is \"$currentTag\", project version is \"$buildVersion\"

if [ "x$currentTag" != "x$buildVersion" ]; then
  echo "current tag version \"$currentTag\" does not match project version \"$buildVersion\""
  exit 1
fi

echo "TBD - actual publish code"