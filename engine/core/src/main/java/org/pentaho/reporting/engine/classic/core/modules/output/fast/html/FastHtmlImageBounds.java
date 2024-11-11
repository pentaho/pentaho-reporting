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
package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

public class FastHtmlImageBounds {
  private long width;
  private long height;
  private long contentWidth;
  private long contentHeight;

  public FastHtmlImageBounds( final long width, final long height, final long contentWidth, final long contentHeight ) {
    this.width = width;
    this.height = height;
    this.contentWidth = contentWidth;
    this.contentHeight = contentHeight;
  }

  public long getWidth() {
    return width;
  }

  public long getHeight() {
    return height;
  }

  public long getContentWidth() {
    return contentWidth;
  }

  public long getContentHeight() {
    return contentHeight;
  }
}
