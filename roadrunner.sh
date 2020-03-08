#!/bin/bash

RR_PROJECT_PATH=/home/bing/workspace/RoadRunner
RR_BUILD_PATH=${RR_PROJECT_PATH}/build

echo "Compiling whole project ..."
cd $RR_PROJECT_PATH
ant build
cd -

source ${RR_PROJECT_PATH}/msetup
##################################################################

APP_HOME=`pwd`
APP_JAR=${APP_HOME}/build/jar/test.jar
APP_CLASS="test.Test"
APP_ARGS=
RR_TOOL="P"
RR_ARGS=" -classpath=${APP_JAR} -tool=${RR_TOOL} -maxTid=$[AVAIL_PROCS + 10]"

LOG_FILE=${APP_HOME}/log/log.txt
if [ ! $# -eq 0 ]
then
  RR_TOOL=$1
  APP_CLASS=$2
fi


if [ ! -f ${LOG_FILE} ]
then
  echo "Log file does not exist and create it ..."
  mkdir -p ${APP_HOME}/log
  touch ${LOG_FILE}
fi
echo "Using RoadRunner to extract dynamc data trace for Java Thread ..."
echo "Using Specify RoadRunner Configuration: $RR_ARGS"
echo "App jar: ${APP_JAR}"
echo "App main class: ${APP_CLASS}"
echo "App arguments: ${APP_ARGS}"
echo "Log file: ${LOG_FILE}"


set -x
rrrun ${RR_ARGS} ${APP_CLASS} ${APP_ARGS} &> ${LOG_FILE}

