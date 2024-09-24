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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

public class XmlPageOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature WRITE_RESOURCEKEYS =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "xml.write-resourcekeys" );

  public XmlPageOutputProcessorMetaData() {
    this( new AWTFontRegistry() );
  }

  public XmlPageOutputProcessorMetaData( final FontRegistry registry ) {
    super( new DefaultFontStorage( registry ) );
    setFamilyMapping( null, "Helvetica" );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.PAGEBREAKS );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.WriteResourceKeys" ) ) ) {
      addFeature( WRITE_RESOURCEKEYS );
    }
  }

  public String getNormalizedFontFamilyName( final String name ) {
    final String mappedName = super.getNormalizedFontFamilyName( name );
    if ( FontMappingUtility.isSerif( mappedName ) ) {
      return "Times";
    }
    if ( FontMappingUtility.isSansSerif( mappedName ) ) {
      return "Helvetica";
    }
    if ( FontMappingUtility.isCourier( mappedName ) ) {
      return "Courier";
    }
    if ( FontMappingUtility.isSymbol( mappedName ) ) {
      return "Symbol";
    }
    return mappedName;
  }

  public String getExportDescriptor() {
    return "pageable/xml";
  }
}
