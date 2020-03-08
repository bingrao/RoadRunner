#!/bin/bash

PROJECT_PATH=/home/bing/workspace/RoadRunner
BUILD_PATH=${PROJECT_PATH}/build
LOG_File=`pwd`/log/log.txt

TARGET="test.Test"
TOOL="PE"

if [ ! $# -eq 0 ]
then
  TOOL=$1
  TARGET=$2
fi

if [ -d "${BUILD_PATH}" ]
then
  echo "The project is already compiled ..."
else
  echo "Compiling whole project ..."
  cd $PROJECT_PATH
  ant
  cd -
fi

if ! [ -x "$(command -v rrrun)" ]
then
  echo 'Error: rrrun command is not added to Path'
  source ${PROJECT_PATH}/msetup
fi


if [ -f ${LOG_FILE} ]
then
  echo "Log file does not exist and create it ..."
  mkdir -p ${PROJECT_PATH}/log
  touch ${LOG_File}
fi

set -x
rrrun -classpath=${BUILD_PATH}/jar/test.jar -tool=${TOOL} ${TARGET} &> ${LOG_File}

