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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;

public class TextOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final OutputProcessorFeature.NumericOutputProcessorFeature CHAR_WIDTH =
      new OutputProcessorFeature.NumericOutputProcessorFeature( "txt.character-width-pt" );
  public static final OutputProcessorFeature.NumericOutputProcessorFeature CHAR_HEIGHT =
      new OutputProcessorFeature.NumericOutputProcessorFeature( "txt.character-height-pt" );

  public TextOutputProcessorMetaData( final float lpi, final float cpi ) {
    super( new DefaultFontStorage( new MonospaceFontRegistry( lpi, cpi ) ) );
    setNumericFeatureValue( TextOutputProcessorMetaData.CHAR_WIDTH, 72.0 / cpi );
    setNumericFeatureValue( TextOutputProcessorMetaData.CHAR_HEIGHT, 72.0 / lpi );
    // the plain text target does not support arabic text at all.
    removeFeature( OutputProcessorFeature.COMPLEX_TEXT );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.PAGEBREAKS );
    removeFeature( OutputProcessorFeature.COMPLEX_TEXT );

    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }

    // plain text reports must never have that setting enabled.
    removeFeature( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC );

    addFeature( OutputProcessorFeature.IGNORE_ROTATION );
  }

  public boolean isFeatureSupported( final OutputProcessorFeature.BooleanOutputProcessorFeature feature ) {
    if ( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC.equals( feature ) ) {
      // This would mess up our beautiful text processing. We hardcode the value here so that no evil (or stupid)
      // user could ever override it.
      return false;
    }
    return super.isFeatureSupported( feature );
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return "pageable/text";
  }
}
