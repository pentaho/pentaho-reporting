/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;

import java.util.ArrayList;

public class BundleWriterHandlerRegistry {
  private static final BundleWriterHandlerRegistry instance = new BundleWriterHandlerRegistry();
  private ArrayList<Class<? extends BundleWriterHandler>> masterreportWriteHandlers;
  private ArrayList<Class<? extends BundleWriterHandler>> subreportWriteHandlers;
  private DefaultTagDescription writerTagDescription;

  public static BundleWriterHandlerRegistry getInstance() {
    return instance;
  }

  private BundleWriterHandlerRegistry() {
    masterreportWriteHandlers = new ArrayList<Class<? extends BundleWriterHandler>>();
    subreportWriteHandlers = new ArrayList<Class<? extends BundleWriterHandler>>();
    writerTagDescription = new DefaultTagDescription();
  }

  public BundleWriterHandler[] getWriteHandlers( final boolean master ) {
    final ArrayList<Class<? extends BundleWriterHandler>> list;
    if ( master ) {
      list = masterreportWriteHandlers;
    } else {
      list = subreportWriteHandlers;
    }
    final ArrayList<BundleWriterHandler> retval = new ArrayList<BundleWriterHandler>();
    for ( int i = 0; i < list.size(); i++ ) {
      try {
        final Class<? extends BundleWriterHandler> c = list.get( i );
        retval.add( c.newInstance() );
      } catch ( Exception e ) {
        // ignore
      }
    }
    return retval.toArray( new BundleWriterHandler[retval.size()] );
  }

  public void registerMasterReportHandler( final Class<? extends BundleWriterHandler> writeHandler ) {
    if ( writeHandler == null ) {
      return;
    }
    masterreportWriteHandlers.add( writeHandler );
  }

  public void registerSubReportHandler( final Class<? extends BundleWriterHandler> writeHandler ) {
    if ( writeHandler == null ) {
      return;
    }
    subreportWriteHandlers.add( writeHandler );
  }

  public void setElementHasCData( final String namespaceUri, final String tagName, final boolean hasCData ) {
    writerTagDescription.setElementHasCData( namespaceUri, tagName, hasCData );
  }

  public void setNamespaceHasCData( final String namespaceUri, final boolean hasCData ) {
    writerTagDescription.setNamespaceHasCData( namespaceUri, hasCData );
  }

  public DefaultTagDescription createWriterTagDescription() {
    return writerTagDescription.clone();
  }
}
