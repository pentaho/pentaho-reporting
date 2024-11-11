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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

/**
 * Creation-Date: 20.10.2007, 16:33:56
 *
 * @author Thomas Morgner
 */
public class XmlTableOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature WRITE_RESOURCEKEYS =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "xml.write-resourcekeys" );

  public static final int PAGINATION_NONE = 0;
  public static final int PAGINATION_MANUAL = 1;
  public static final int PAGINATION_FULL = 2;
  private int paginationMode;

  public XmlTableOutputProcessorMetaData() {
    this( PAGINATION_NONE );
  }

  public XmlTableOutputProcessorMetaData( final int paginationMode ) {
    this( paginationMode, new AWTFontRegistry() );
  }

  public XmlTableOutputProcessorMetaData( final int paginationMode, final FontRegistry fontRegistry ) {
    super( new DefaultFontStorage( fontRegistry ) );
    this.paginationMode = paginationMode;
    setFamilyMapping( null, "Helvetica" );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xml.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xml.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xml.ShapeAsContent" ) ) ) {
      addFeature( AbstractTableOutputProcessor.SHAPES_CONTENT );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xml.WriteResourceKeys" ) ) ) {
      addFeature( WRITE_RESOURCEKEYS );
    }

    if ( paginationMode == HtmlOutputProcessorMetaData.PAGINATION_FULL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
    } else if ( paginationMode == HtmlOutputProcessorMetaData.PAGINATION_MANUAL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
    } else {
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
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
    switch ( paginationMode ) {
      case HtmlOutputProcessorMetaData.PAGINATION_FULL:
        return "table/xml+pagination";
      case HtmlOutputProcessorMetaData.PAGINATION_MANUAL:
        return "table/xml+flow";
      default:
        return "table/xml+stream";
    }
  }
  /*
   * public boolean isExtraContentElement(final StyleSheet style, final ReportAttributeMap attributes) { return true; }
   */
}
