/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
