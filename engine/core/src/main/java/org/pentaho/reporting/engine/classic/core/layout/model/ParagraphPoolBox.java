/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 03.04.2007, 13:38:24
 *
 * @author Thomas Morgner
 */
public final class ParagraphPoolBox extends InlineRenderBox {
  // This class makes sure that the lineheight is shared across all clones.
  private static class LineHeightWrapper {
    private long lineHeight;

    public long getLineHeight() {
      return lineHeight;
    }

    public void setLineHeight( final long lineHeight ) {
      this.lineHeight = lineHeight;
    }
  }

  private LineHeightWrapper lineHeightWrapper;

  public ParagraphPoolBox( final StyleSheet style, final InstanceID instanceID, final ReportStateKey stateKey ) {
    super( style, instanceID, BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, stateKey );
    lineHeightWrapper = new LineHeightWrapper();
  }

  public long getLineHeight() {
    return lineHeightWrapper.getLineHeight();
  }

  public void setLineHeight( final long lineHeight ) {
    lineHeightWrapper.setLineHeight( lineHeight );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_LINEBOX;
  }
}
