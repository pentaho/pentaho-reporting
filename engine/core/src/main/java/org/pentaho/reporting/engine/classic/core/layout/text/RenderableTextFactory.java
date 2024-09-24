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
