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
