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
