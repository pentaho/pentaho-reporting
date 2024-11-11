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
# Change Date: 2028-08-13
# ******************************************************************************

mkdir -p -- "$PWD/.uninstalled"
INSTALLEDDIR=$PWD/.uninstalled
mkdir -p -- $INSTALLEDDIR/resources

if [ -e $PWD/resources/kettle-lifecycle-listeners.xml ];then mv $PWD/resources/kettle-lifecycle-listeners.xml $INSTALLEDDIR/resources/kettle-lifecycle-listeners.xml; fi
if [ -e $PWD/resources/kettle-registry-extensions.xml ];then mv $PWD/resources/kettle-registry-extensions.xml $INSTALLEDDIR/resources/kettle-registry-extensions.xml; fi

if [ -e $PWD/drivers ];then mv $PWD/drivers $INSTALLEDDIR/drivers ; fi  
if [ -e $PWD/system ];then mv $PWD/system $INSTALLEDDIR/system ; fi
mkdir -p -- $INSTALLEDDIR/lib
mv "$PWD"/lib/org.apache.karaf*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/org.apache.felix*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/osgi.*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/org.osgi*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/pax-logging-log4j2*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/jline*.jar $INSTALLEDDIR/lib/
mv "$PWD"/lib/jna-platform*.jar $INSTALLEDDIR/lib/