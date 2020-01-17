#!/bin/bash

if [ -z "${MELITA_HOME}" ]
then
   export CLASSPATH=${PWD}/AmplStat:${PWD}/DBStat:${PWD}/DegPrimerTree:${PWD}/DPDesign:${PWD}/DPStats:${PWD}/Extract16S:${PWD}/ReadAmplSet:${PWD}/Test:${PWD}/Core
else
   export CLASSPATH=${MELITA_HOME}/AmplStat:${MELITA_HOME}/DBStat:${MELITA_HOME}/DegPrimerTree:${MELITA_HOME}/DPDesign:${MELITA_HOME}/DPStats:${MELITA_HOME}/Extract16S:${MELITA_HOME}/ReadAmplSet:${MELITA_HOME}/Test:${MELITA_HOME}/Core
fi
java ${1}Main $2 $3
