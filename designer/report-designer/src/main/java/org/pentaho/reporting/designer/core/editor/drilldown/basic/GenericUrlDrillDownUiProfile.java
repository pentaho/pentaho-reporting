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


package org.pentaho.reporting.designer.core.editor.drilldown.basic;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 15:12:00
 *
 * @author Thomas Morgner.
 */
public class GenericUrlDrillDownUiProfile extends XulDrillDownUiProfile {
  public GenericUrlDrillDownUiProfile() throws IllegalStateException {
    super( new String[] { "generic-url", "local-url" } );//NON-NLS
  }

  public int getOrderKey() {
    return 1000;
  }
}
