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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

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
