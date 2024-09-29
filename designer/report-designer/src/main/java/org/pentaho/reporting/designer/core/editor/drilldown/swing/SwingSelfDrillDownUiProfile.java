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


package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

import java.util.Locale;

/**
 * Implementation of DrillDownUiProfile for Swing version of self-drilldown.xul dialog.
 *
 * @author Aleksandr Kozlov
 */
public class SwingSelfDrillDownUiProfile implements DrillDownUiProfile {

  /** Drill down meta data. */
  private DrillDownProfile drillDownProfile;

  /** The names that SwingSelfDrillDownUi are able to handle. */
  private static final String[] NAMES_HANDLE = { "self" };

  /** Default name of the profile. */
  public static final String NAME_DEFAULT = NAMES_HANDLE[ 0 ];

  /**
   * Create an implementation of DrillDownUiProfile for Swing version of self-drilldown.xul dialog.
   */
  public SwingSelfDrillDownUiProfile() {
    drillDownProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( NAME_DEFAULT );
  }

  /**
   * Create an instance of DrillDownUi for Swing version of self-drilldown.xul dialog.
   *
   * @return an instance of SwingSelfDrillDownUi
   */
  @Override
  public DrillDownUi createUI() {
    return new SwingSelfDrillDownUi();
  }

  /**
   * Get name of the DrillDownUi to display.
   *
   * @return name for display.
   */
  @Override
  public String getDisplayName() {
    return drillDownProfile.getGroupDisplayName( Locale.getDefault() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canHandle( String profileName ) {
    for ( int i = 0; i < NAMES_HANDLE.length; i++ ) {
      final String name = NAMES_HANDLE[ i ];
      if ( name.equals( profileName ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getOrderKey() {
    return 500;
  }
}
