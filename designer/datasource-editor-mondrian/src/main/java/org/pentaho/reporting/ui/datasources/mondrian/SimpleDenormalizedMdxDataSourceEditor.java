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


package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleDenormalizedMDXDataFactory;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class SimpleDenormalizedMdxDataSourceEditor extends SimpleMondrianDataSourceEditor {

  public SimpleDenormalizedMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public SimpleDenormalizedMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public SimpleDenormalizedMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "SimpleDenormalizedMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "MondrianDataSourceEditor.SimpleDenormalized";
  }

  protected AbstractMDXDataFactory createDataFactory() {
    final SimpleDenormalizedMDXDataFactory returnDataFactory = new SimpleDenormalizedMDXDataFactory();
    configureConnection( returnDataFactory );
    return returnDataFactory;
  }


}
