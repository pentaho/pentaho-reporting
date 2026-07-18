/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleBandedMDXDataFactory;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class SimpleBandedMdxDataSourceEditor extends SimpleMondrianDataSourceEditor {
  public SimpleBandedMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public SimpleBandedMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public SimpleBandedMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "SimpleBandedMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "MondrianDataSourceEditor.SimpleBanded";
  }

  protected AbstractMDXDataFactory createDataFactory() {
    final SimpleBandedMDXDataFactory returnDataFactory = new SimpleBandedMDXDataFactory();
    configureConnection( returnDataFactory );
    return returnDataFactory;
  }

}
