/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleManifestXmlFactoryModule;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleManifestXmlResourceFactory;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleMetaDataXmlFactoryModule;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleMetaDataXmlResourceFactory;

public class LibDocBundleBoot extends AbstractBoot {
  private static LibDocBundleBoot instance;

  public static synchronized LibDocBundleBoot getInstance() {
    if ( LibDocBundleBoot.instance == null ) {
      LibDocBundleBoot.instance = new LibDocBundleBoot();
    }
    return LibDocBundleBoot.instance;
  }

  private LibDocBundleBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/docbundle/libdocbundle.properties",
        "/libdocbundle.properties", true, LibDocBundleBoot.class );
  }

  protected void performBoot() {
    BundleMetaDataXmlResourceFactory.register( BundleMetaDataXmlFactoryModule.class );
    BundleManifestXmlResourceFactory.register( BundleManifestXmlFactoryModule.class );
  }

  protected ProjectInformation getProjectInfo() {
    return LibDocBundleInfo.getInstance();
  }
}
