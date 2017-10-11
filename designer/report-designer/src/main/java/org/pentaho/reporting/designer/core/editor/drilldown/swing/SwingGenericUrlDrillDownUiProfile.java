/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

import java.util.Locale;

/**
 * Implementation of DrillDownUiProfile for Swing version of generic-url-drilldown.xul dialog.
 *
 * @author Aleksandr Kozlov
 */
public class SwingGenericUrlDrillDownUiProfile implements DrillDownUiProfile {

  /** Drill down meta data. */
  private DrillDownProfile drillDownProfile;

  /** The names that SwingGenericUrlDrillDownUi are able to handle. */
  private static final String[] NAMES_HANDLE = { "generic-url", "local-url" };

  /** Default name of the profile. */
  public static final String NAME_DEFAULT = NAMES_HANDLE[ 0 ];

  /**
   * Create an implementation of DrillDownUiProfile for Swing version of generic-url-drilldown.xul dialog.
   */
  public SwingGenericUrlDrillDownUiProfile() {
    drillDownProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( NAME_DEFAULT );
  }

  /**
   * Create an instance of DrillDownUi for Swing version of generic-url-drilldown.xul dialog.
   *
   * @return an instance of SwingGenericUrlDrillDownUi
   */
  @Override
  public DrillDownUi createUI() {
    return new SwingGenericUrlDrillDownUi();
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
