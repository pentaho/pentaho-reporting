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

package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

public class LocalFontMetricsFactory implements FontMetricsFactory {
  private static class LocalFontType extends FontType {
    private LocalFontType() {
      super( "Local" );
    }
  }

  private LocalFontRegistry registry;
  public static final FontType LOCAL = new LocalFontType();

  public LocalFontMetricsFactory( final LocalFontRegistry registry ) {
    this.registry = registry;
  }

  public FontMetrics createMetrics( final FontIdentifier identifier, final FontContext context ) {
    return new LocalFontMetrics( registry.getFontMetricsBase( identifier ), (float) context.getFontSize() );
  }
}
