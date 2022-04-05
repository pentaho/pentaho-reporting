#!/bin/bash

# -----------------------------------------------------------------------------------------------
# This program is free software; you can redistribute it and/or modify it under the
# terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
# Foundation.
#
# You should have received a copy of the GNU Lesser General Public License along with this
# program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
# or from the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Lesser General Public License for more details.
#
# Copyright (c) 2013 - ${copyright.year} Hitachi Vantara. All rights reserved.
# -----------------------------------------------------------------------------------------------

#
#  WARNING: Hitachi Vantara Report Designer needs JDK 11 or newer to run.
#

DIR=$( cd "$( dirname "$0" )" && pwd )

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

JAVA_LOCALE_COMPAT=
JAVA_ADD_OPENS=
if $($_PENTAHO_JAVA -version 2>&1 | grep "version \"11\..*" > /dev/null )
then
  JAVA_LOCALE_COMPAT="-Djava.locale.providers=COMPAT,SPI"
  JAVA_ADD_OPENS="--add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.jar=ALL-UNNAMED  --add-opens java.desktop/com.apple.eawt=ALL-UNNAMED"
fi

if [[ "$OSTYPE" == "darwin"* ]]; then 
	"$_PENTAHO_JAVA" $JAVA_ADD_OPENS -Xms1024m -Xmx2048m -Dapple.laf.useScreenMenuBar=true $JAVA_LOCALE_COMPAT -jar "$DIR/launcher.jar" $@
else
	"$_PENTAHO_JAVA" $JAVA_ADD_OPENS -Xms1024m -Xmx2048m $JAVA_LOCALE_COMPAT -jar "$DIR/launcher.jar" $@
fi
