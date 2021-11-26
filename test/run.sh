#!/bin/sh
cd $(dirname $0)

mkdir -p target
cd target

cd ..

cd ../complete

./mvnw clean package -Dspring.cloud.vault.app-role.secret-id=${APP_SECRET_ID}
ret=$?
if [ $ret -ne 0 ]; then
  exit $ret
fi
rm -rf target


exit