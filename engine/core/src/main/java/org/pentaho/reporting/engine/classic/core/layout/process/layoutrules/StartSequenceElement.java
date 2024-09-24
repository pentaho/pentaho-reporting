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

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

/**
 * Represents the opening of an inline element and represents the respective border. There is no break after that
 * element.
 *
 * @author Thomas Morgner
 */
public class StartSequenceElement implements InlineSequenceElement {
  public static final InlineSequenceElement INSTANCE = new StartSequenceElement();

  private StartSequenceElement() {
  }

  /**
   * The width of the element. This is the minimum width of the element.
   *
   * @return
   */
  public long getMinimumWidth( final RenderNode node ) {
    final InlineRenderBox box = (InlineRenderBox) node;
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();
    return blp.getBorderLeft() + bdef.getPaddingLeft() + blp.getMarginLeft();
  }

  /**
   * The extra-space width for an element. Some elements can expand to fill some more space (justified text is a good
   * example, adding some space between the letters of each word to reduce the inner-word spacing).
   *
   * @return
   */
  public long getMaximumWidth( final RenderNode node ) {
    return getMinimumWidth( node );
  }

  public boolean isPreserveWhitespace( final RenderNode node ) {
    final InlineRenderBox box = (InlineRenderBox) node;
    return box.getStaticBoxLayoutProperties().isPreserveSpace();
  }

  public int getClassification() {
    return START;
  }

  public Classification getType() {
    return Classification.START;
  }
}
