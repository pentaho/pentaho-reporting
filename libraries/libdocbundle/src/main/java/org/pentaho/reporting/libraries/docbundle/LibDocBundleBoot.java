/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
