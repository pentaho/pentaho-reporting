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
