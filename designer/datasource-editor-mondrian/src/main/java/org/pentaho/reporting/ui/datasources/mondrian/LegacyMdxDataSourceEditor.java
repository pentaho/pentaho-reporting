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
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.LegacyBandedMDXDataFactory;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class LegacyMdxDataSourceEditor extends MondrianDataSourceEditor {

  public LegacyMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public LegacyMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public LegacyMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "LegacyMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "MondrianDataSourceEditor.Legacy";
  }

  protected AbstractMDXDataFactory createDataFactory() {
    final LegacyBandedMDXDataFactory returnDataFactory = new LegacyBandedMDXDataFactory();
    configureConnection( returnDataFactory );
    configureQueries( returnDataFactory );
    return returnDataFactory;
  }
}
