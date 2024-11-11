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


package org.pentaho.reporting.engine.classic.core.testsupport.graphics;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugOutputProcessorMetaData;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TestFontMetrics extends FontMetrics {
  private DebugOutputProcessorMetaData metaData;

  public TestFontMetrics( final Font font ) {
    super( font );
    metaData = new DebugOutputProcessorMetaData();
    metaData.initialize( ClassicEngineBoot.getInstance().getGlobalConfig() );
  }

  /**
   * Returns the bounds for the character with the maximum bounds in the specified <code>Graphics</code> context.
   *
   * @param context
   *          the specified <code>Graphics</code> context
   * @return a <code>Rectangle2D</code> that is the bounding box for the character with the maximum bounds.
   * @see java.awt.Font#getMaxCharBounds(java.awt.font.FontRenderContext)
   */
  public Rectangle2D getMaxCharBounds( final Graphics context ) {
    final Font baseFont = getFont();
    final String name = baseFont.getName();
    final org.pentaho.reporting.libraries.fonts.registry.FontMetrics fontMetrics =
        metaData.getFontMetrics( name, baseFont.getSize2D(), baseFont.isBold(), baseFont.isItalic(), "UTF-8", false,
            false );
    return new Rectangle2D.Double( 0, -FontStrictGeomUtility.toExternalValue( fontMetrics.getMaxAscent() ),
        FontStrictGeomUtility.toExternalValue( fontMetrics.getMaxCharAdvance() ), FontStrictGeomUtility
            .toExternalValue( fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + fontMetrics.getLeading() ) );
  }
}
