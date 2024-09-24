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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
