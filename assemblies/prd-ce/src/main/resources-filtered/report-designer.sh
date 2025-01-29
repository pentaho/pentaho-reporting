#!/bin/bash
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2029-07-20
# ******************************************************************************


#
#  WARNING: Hitachi Vantara Report Designer needs JDK 11 or newer to run.
#

DIR=$( cd "$( dirname "$0" )" && pwd )

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

JAVA_LOCALE_COMPAT=
JAVA_ADD_OPENS=

JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.io=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang.reflect=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.net=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.security=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.util=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.file=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.ftp=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.reflect.misc=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management.openmbean=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.naming/com.sun.jndi.ldap=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.math=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.nio=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.desktop/com.sun.beans.editors=ALL-UNNAMED"
JAVA_LOCALE_COMPAT="-Djava.locale.providers=COMPAT,SPI"

if [[ "$OSTYPE" == "darwin"* ]]; then
	JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.desktop/com.apple.eawt=ALL-UNNAMED"
	"$_PENTAHO_JAVA" $JAVA_ADD_OPENS -Xms1024m -Xmx2048m -Dapple.laf.useScreenMenuBar=true $JAVA_LOCALE_COMPAT -jar "$DIR/launcher.jar" $@
else
	"$_PENTAHO_JAVA" $JAVA_ADD_OPENS -Xms1024m -Xmx2048m $JAVA_LOCALE_COMPAT -jar "$DIR/launcher.jar" $@
fi
