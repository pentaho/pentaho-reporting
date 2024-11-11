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
