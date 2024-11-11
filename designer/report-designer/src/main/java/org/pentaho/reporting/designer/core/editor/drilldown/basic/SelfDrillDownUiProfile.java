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


package org.pentaho.reporting.designer.core.editor.drilldown.basic;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 15:12:00
 *
 * @author Thomas Morgner.
 */
public class SelfDrillDownUiProfile extends XulDrillDownUiProfile {
  public SelfDrillDownUiProfile() throws IllegalStateException {
    super( "self" );
  }

  public int getOrderKey() {
    return 500;
  }
}
