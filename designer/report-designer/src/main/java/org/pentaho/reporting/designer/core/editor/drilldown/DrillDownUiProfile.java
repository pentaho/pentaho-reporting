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

package org.pentaho.reporting.designer.core.editor.drilldown;

/**
 * Instances of this class must override equals and hashcode or you will be doomed!
 * <p/>
 *
 * @author Thomas Morgner.
 */
public interface DrillDownUiProfile {
  public DrillDownUi createUI();

  public String getDisplayName();

  public boolean canHandle( String profileName );

  public int getOrderKey();
}
