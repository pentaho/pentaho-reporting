#!/bin/sh

#
#  WARNING: Pentaho Report Designer needs JDK 1.6 or newer to run.
#

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`
cd -

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

"$_PENTAHO_JAVA" -XX:MaxPermSize=512m -jar "$DIR/launcher.jar" $@
