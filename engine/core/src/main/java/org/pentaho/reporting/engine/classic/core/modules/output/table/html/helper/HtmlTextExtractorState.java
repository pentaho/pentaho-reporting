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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.util.Arrays;

public class HtmlTextExtractorState {
  private static final StyleBuilder.StyleCarrier[] EMPTY =
      new StyleBuilder.StyleCarrier[StyleBuilder.CSSKeys.values().length];
  private StyleBuilder.StyleCarrier[] style;
  private boolean writtenTag;
  private HtmlTextExtractorState parent;

  public HtmlTextExtractorState( HtmlTextExtractorState parent, final boolean writtenTag ) {
    this( parent, writtenTag, null );
  }

  public HtmlTextExtractorState( HtmlTextExtractorState parent, final boolean writtenTag,
      final StyleBuilder.StyleCarrier[] style ) {
    this.parent = parent;
    this.writtenTag = writtenTag;
    if ( style == null ) {
      if ( parent == null ) {
        this.style = EMPTY.clone();
      } else {
        this.style = parent.style;
      }
    } else {
      this.style = style;
    }
  }

  public HtmlTextExtractorState getParent() {
    return parent;
  }

  public StyleBuilder.StyleCarrier[] getStyle() {
    return style;
  }

  public boolean isWrittenTag() {
    return writtenTag;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final HtmlTextExtractorState that = (HtmlTextExtractorState) o;

    if ( writtenTag != that.writtenTag ) {
      return false;
    }
    if ( !Arrays.equals( style, that.style ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = style != null ? Arrays.hashCode( style ) : 0;
    result = 31 * result + ( writtenTag ? 1 : 0 );
    return result;
  }
}
