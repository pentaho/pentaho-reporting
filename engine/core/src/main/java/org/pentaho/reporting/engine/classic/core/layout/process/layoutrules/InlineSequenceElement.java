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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * A sequence element. Usually all elements get their maximum width. There are only a few special cases, where the
 * minimum width needs to be considered:
 * <p/>
 * * The element is an inline-block and there is not enough space to print he complete element. The element is
 * guaranteed to always get its minimum width.
 *
 * @author Thomas Morgner
 */
public interface InlineSequenceElement {
  public enum Classification {
    START, CONTENT, END;
  }

  public static final int START = 0;
  public static final int CONTENT = 1;
  public static final int END = 2;

  /**
   * The minimum width of the element. This is the minimum width of the element.
   *
   * @return
   */
  public long getMinimumWidth( final RenderNode node );

  /**
   * The maximum width an element wants to take. This returns the preferred size; even if offered more space, an element
   * would not consume more than that.
   *
   * @return
   */
  public long getMaximumWidth( final RenderNode node );

  public boolean isPreserveWhitespace( final RenderNode node );

  public int getClassification();

  public Classification getType();
}
