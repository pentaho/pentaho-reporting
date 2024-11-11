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


package org.pentaho.reporting.libraries.fonts.truetype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.fonts.io.FileFontDataInputSource;
import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is still experimental. At a later time, this class should be refactored to work without file references.
 *
 * @author Thomas Morgner
 */
public class TrueTypeFontMetricsFactory implements FontMetricsFactory {
  private static final Log logger = LogFactory.getLog( TrueTypeFontMetricsFactory.class );
  private HashMap<TrueTypeFontIdentifier, ScalableTrueTypeFontMetrics> fontRecords;

  public TrueTypeFontMetricsFactory() {
    this.fontRecords = new HashMap<TrueTypeFontIdentifier, ScalableTrueTypeFontMetrics>();
  }

  public FontMetrics createMetrics( final FontIdentifier record,
                                    final FontContext context ) {
    if ( FontType.OPENTYPE.equals( record.getFontType() ) == false ) {
      throw new IllegalArgumentException
        ( "This identifier does not belong to the OpenType-font system." );
    }

    final TrueTypeFontIdentifier ttfId = (TrueTypeFontIdentifier) record;

    final ScalableTrueTypeFontMetrics fromCache =
      (ScalableTrueTypeFontMetrics) fontRecords.get( ttfId );
    if ( fromCache != null ) {
      return new TrueTypeFontMetrics( ttfId, fromCache, context.getFontSize() );
    }

    try {
      final String fontSource = ttfId.getFontSource();
      final FontDataInputSource fdis =
        new FileFontDataInputSource( new File( fontSource ) );
      final TrueTypeFont font = new TrueTypeFont( fdis );
      final ScalableTrueTypeFontMetrics fontMetrics =
        new ScalableTrueTypeFontMetrics( font );
      this.fontRecords.put( ttfId, fontMetrics );
      return new TrueTypeFontMetrics( ttfId, fontMetrics, context.getFontSize() );
    } catch ( IOException e ) {
      logger.warn( "Unable to read the font.", e );
      // todo: We should throw a better exception instead, shouldnt we?
      throw new IllegalStateException();
    }
  }
}
