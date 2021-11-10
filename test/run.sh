#!/bin/sh
cd $(dirname $0)

mkdir -p target
cd target

cd ..

cd ../complete

./mvnw clean package
ret=$?
if [ $ret -ne 0 ]; then
  exit $ret
fi
rm -rf target


exit