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

package org.pentaho.reporting.libraries.docbundle;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleMetaDataEntryReadHandler;
import org.pentaho.reporting.libraries.docbundle.metadata.parser.BundleMetaDataEntryReadHandlerFactory;

public class BundleMetaDataReadHandlerTest extends TestCase {
  public BundleMetaDataReadHandlerTest() {
  }

  public BundleMetaDataReadHandlerTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibDocBundleBoot.getInstance().start();
  }

  public void testCreateDateFactoryExists() {
    final String uri = "urn:oasis:names:tc:opendocument:xmlns:meta:1.0";
    final String tagName = "creation-date";
    final BundleMetaDataEntryReadHandlerFactory handlerFactory = BundleMetaDataEntryReadHandlerFactory.getInstance();
    final BundleMetaDataEntryReadHandler readHandler = handlerFactory.getHandler( uri, tagName );
    assertNotNull( readHandler );
  }
}
