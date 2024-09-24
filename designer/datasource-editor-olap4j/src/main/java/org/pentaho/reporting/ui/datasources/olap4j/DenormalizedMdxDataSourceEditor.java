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

package org.pentaho.reporting.ui.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.DenormalizedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class DenormalizedMdxDataSourceEditor extends Olap4JDataSourceEditor {

  public DenormalizedMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public DenormalizedMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public DenormalizedMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "DenormalizedMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "Olap4JDataSourceEditor.Denormalized";
  }

  protected AbstractNamedMDXDataFactory createDataFactory() {
    final OlapConnectionProvider connectionProvider = createConnectionProvider();
    if ( connectionProvider == null ) {
      return null;
    }
    final DenormalizedMDXDataFactory returnDataFactory = new DenormalizedMDXDataFactory( connectionProvider );
    configureQueries( returnDataFactory );
    return returnDataFactory;

  }
}
