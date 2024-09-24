/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

/**
 * Creation-Date: Dec 18, 2006, 1:05:00 PM
 *
 * @author Thomas Morgner
 */
public class BundleMetaDataEntryReadHandlerFactory
  extends AbstractReadHandlerFactory<BundleMetaDataEntryReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.libraries.docbundle.metadata.metadata-factory.";

  private static BundleMetaDataEntryReadHandlerFactory readHandlerFactory;

  public static synchronized BundleMetaDataEntryReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      final Configuration config = LibDocBundleBoot.getInstance().getGlobalConfig();

      readHandlerFactory = new BundleMetaDataEntryReadHandlerFactory();
      readHandlerFactory.configure( config, BundleMetaDataEntryReadHandlerFactory.PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }


  private BundleMetaDataEntryReadHandlerFactory() {
  }

  protected Class<BundleMetaDataEntryReadHandler> getTargetClass() {
    return BundleMetaDataEntryReadHandler.class;
  }
}
