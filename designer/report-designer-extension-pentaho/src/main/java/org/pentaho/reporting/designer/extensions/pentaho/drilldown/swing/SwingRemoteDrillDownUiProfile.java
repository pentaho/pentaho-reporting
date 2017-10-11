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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown.swing;

import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

import java.util.Locale;

/**
 * Profile of the Swing analog for sugar-xaction-drilldown.xul dialog.
 *
 * @author Aleksandr Kozlov
 */
public class SwingRemoteDrillDownUiProfile implements DrillDownUiProfile {

  /** Drill down meta data. */
  private DrillDownProfile drillDownProfile;

  /** The names that SwingRemoteDrillDownUi is able to handle. */
  private static final String[] NAMES_HANDLE = { "remote-sugar" };

  /** Default name of the profile. */
  public static final String NAME_DEFAULT = NAMES_HANDLE[ 0 ];

  /**
   * Create an implementation of SwingRemoteDrillDownUiProfile for Swing version of sugar-xaction-drilldown.xul dialog.
   */
  public SwingRemoteDrillDownUiProfile() {
    drillDownProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( NAME_DEFAULT );
  }

  /**
   * Create an instance of DrillDownUi for Swing version of the sugar-xaction-drilldown.xul dialog.
   *
   * @return an instance of SwingRemoteDrillDownUi
   */
  @Override
  public DrillDownUi createUI() {
    return new SwingRemoteDrillDownUi();
  }

  /**
   * Get a name of the DrillDownUi to display.
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
    return 3000;
  }
}
