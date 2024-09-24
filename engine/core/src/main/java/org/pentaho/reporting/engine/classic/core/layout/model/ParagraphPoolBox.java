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
