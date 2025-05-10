#!/bin/sh
##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> .*$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

DEFAULT_JVM_OPTS=""

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

JAVA_EXE="java"

exec "$JAVA_EXE" $DEFAULT_JVM_OPTS -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
