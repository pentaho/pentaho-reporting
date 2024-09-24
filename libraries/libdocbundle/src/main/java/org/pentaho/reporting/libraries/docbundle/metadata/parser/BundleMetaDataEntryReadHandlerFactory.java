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
