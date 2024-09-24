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

package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

/**
 * Anthing that is not text. This could be an image or an inline-block element. For now, we assume that these beasts are
 * not breakable at the end of the line (outer linebreaks).
 *
 * @author Thomas Morgner
 */
public class InlineBoxSequenceElement extends InlineNodeSequenceElement {
  public static final InlineSequenceElement INSTANCE = new InlineBoxSequenceElement();

  private InlineBoxSequenceElement() {
  }

  /**
   * The width of the element. This is the minimum width of the element.
   *
   * @return
   */
  public long getMinimumWidth( final RenderNode node ) {
    final RenderBox box = (RenderBox) node;
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    return box.getMinimumChunkWidth() + blp.getMarginLeft() + blp.getMarginRight();
  }

  public long getMaximumWidth( final RenderNode node ) {
    final RenderBox box = (RenderBox) node;
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    return box.getMaximumBoxWidth() + blp.getMarginLeft() + blp.getMarginRight();
  }

  public boolean isPreserveWhitespace( final RenderNode node ) {
    final RenderBox box = (RenderBox) node;
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    return blp.isPreserveSpace();
  }
}
