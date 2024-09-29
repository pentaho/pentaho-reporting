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


package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleLegacyBandedMDXDataFactory;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class SimpleLegacyMdxDataSourceEditor extends SimpleMondrianDataSourceEditor {

  public SimpleLegacyMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public SimpleLegacyMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public SimpleLegacyMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "SimpleLegacyMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "MondrianDataSourceEditor.SimpleLegacy";
  }

  protected AbstractMDXDataFactory createDataFactory() {
    final SimpleLegacyBandedMDXDataFactory returnDataFactory = new SimpleLegacyBandedMDXDataFactory();
    configureConnection( returnDataFactory );
    return returnDataFactory;
  }
}
