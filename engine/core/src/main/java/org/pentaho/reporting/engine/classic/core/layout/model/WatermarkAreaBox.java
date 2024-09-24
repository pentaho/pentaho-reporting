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

package org.pentaho.reporting.engine.classic.core.layout.model;

/**
 * A marker interface, nothing more.
 *
 * @author Thomas Morgner
 */
public final class WatermarkAreaBox extends PageAreaBox {
  public WatermarkAreaBox() {
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_WATERMARK;
  }
}
