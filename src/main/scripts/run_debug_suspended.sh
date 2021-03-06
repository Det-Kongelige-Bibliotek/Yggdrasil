#!/bin/sh
ProgDir=`dirname "$0"`
. "${ProgDir}/env.sh"

if [ -z "${JAVA_DEBUG_OPTS}" ]; then
  JAVA_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044"
fi

if [ -z "${JAVA_OPTS}" ]; then
  JAVA_OPTS="-Xms256m -Xmx1024m"
fi

"${JAVA}" ${JAVA_DEBUG_OPTS} ${JAVA_OPTS} -D${assembly.config.env.name}="${assembly.config.env.name.ref}" -D${assembly.runningmode.env.name}="${assembly.runningmode.env.name.ref}" -cp "$CP" ${assembly.main.class.name} "$@"
