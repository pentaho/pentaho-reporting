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


package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.*;

/**
 * A Wmf logical region definition.
 */
public class MfLogRegion implements WmfObject {
  private int x;
  private int y;
  private int w;
  private int h;

  public int getType() {
    return OBJ_REGION;
  }

  public MfLogRegion() {
  }

  public void setBounds( final int x, final int y, final int w, final int h ) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public Rectangle getBounds() {
    return new Rectangle( x, y, w, h );
  }
}
