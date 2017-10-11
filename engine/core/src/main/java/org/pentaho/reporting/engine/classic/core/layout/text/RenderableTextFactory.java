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

package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Problem: Text may span more than one chunk, and text may influence the break behaviour of the next chunk.
 * <p/>
 * Possible solution: TextFactory does not return the complete text. It returns the text up to the last whitespace
 * encountered and returns the text chunk only if either finishText has been called or some more text comes in. The ugly
 * sideffect: Text may result in more than one renderable text chunk returned.
 * <p/>
 * If we return lines (broken by an LineBreak-occurence) we can safe us a lot of trouble later.
 *
 * @author Thomas Morgner
 */
public interface RenderableTextFactory {
  /**
   * The text is given as CodePoints.
   *
   * @param text
   * @return
   */
  public RenderNode[] createText( final int[] text, final int offset, final int length, final StyleSheet layoutContext,
      final ElementType elementType, final InstanceID instanceId, final ReportAttributeMap<Object> attributeMap );

  public RenderNode[] finishText();

  public void startText();
}
