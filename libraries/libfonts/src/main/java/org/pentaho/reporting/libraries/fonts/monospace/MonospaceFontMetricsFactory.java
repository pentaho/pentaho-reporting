/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.monospace;

import org.pentaho.reporting.libraries.fonts.registry.DefaultFontNativeContext;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;

/**
 * Creation-Date: 13.05.2007, 13:14:25
 *
 * @author Thomas Morgner
 */
public class MonospaceFontMetricsFactory implements FontMetricsFactory {
  private MonospaceFontMetrics metrics;

  public MonospaceFontMetricsFactory( final float lpi, final float cpi ) {
    this.metrics = new MonospaceFontMetrics( new DefaultFontNativeContext( false, false ), cpi, lpi );
  }

  /**
   * Loads the font metrics for the font identified by the given identifier.
   *
   * @param identifier
   * @param context
   * @return
   */
  public FontMetrics createMetrics( final FontIdentifier identifier, final FontContext context ) {
    return metrics;
  }
}
