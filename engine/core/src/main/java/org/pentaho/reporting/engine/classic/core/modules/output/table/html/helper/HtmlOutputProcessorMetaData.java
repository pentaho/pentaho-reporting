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
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

public class HtmlOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final int PAGINATION_NONE = 0;
  public static final int PAGINATION_MANUAL = 1;
  public static final int PAGINATION_FULL = 2;

  private int paginationMode;

  public HtmlOutputProcessorMetaData( final int paginationMode ) {
    this( HtmlOutputProcessorMetaData.createFontStorage(), paginationMode );
  }

  public HtmlOutputProcessorMetaData( final FontStorage fontStorage, final int paginationMode ) {
    super( fontStorage );
    this.paginationMode = paginationMode;
  }

  private static FontStorage createFontStorage() {
    return new DefaultFontStorage( new AWTFontRegistry() );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );

    final String localStrict =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.StrictLayout" );
    if ( localStrict != null ) {
      if ( "true".equals( localStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    } else {
      final String globalStrict =
          configuration
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout" );
      if ( "true".equals( globalStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );
    final double deviceResolution =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.html.DeviceResolution", 0 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    }

    if ( paginationMode == HtmlOutputProcessorMetaData.PAGINATION_FULL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
    } else if ( paginationMode == HtmlOutputProcessorMetaData.PAGINATION_MANUAL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    } else {
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.ShapeAsContent" ) ) ) {
      addFeature( AbstractTableOutputProcessor.SHAPES_CONTENT );
    }
    if ( "true".equals( configuration
      .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.RotatedTextAsImages" ) ) ) {
      addFeature( AbstractTableOutputProcessor.ROTATED_TEXT_AS_IMAGES );
    }
    if ( "true".equals( configuration
      .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Base64Images" ) ) ) {
      addFeature( AbstractTableOutputProcessor.BASE64_IMAGES );
    }
  }

  public int getPaginationMode() {
    return paginationMode;
  }

  public String getExportDescriptor() {
    switch ( paginationMode ) {
      case HtmlOutputProcessorMetaData.PAGINATION_FULL:
        return "table/html+pagination";
      case HtmlOutputProcessorMetaData.PAGINATION_MANUAL:
        return "table/html+flow";
      default:
        return "table/html+stream";
    }
  }

  /**
   * Checks whether this element provides some extra content that is not part of the visible layout structure. This can
   * be embedded scripts, anchors etc.
   *
   * @param style
   * @param attributes
   * @return
   */
  public boolean isExtraContentElement( final StyleSheet style, final ReportAttributeMap attributes ) {
    if ( isFeatureSupported( OutputProcessorFeature.DETECT_EXTRA_CONTENT ) == false ) {
      return false;
    }
    final Object o = attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
    if ( o instanceof MasterReportType ) {
      return false;
    }
    if ( super.isExtraContentElement( style, attributes ) ) {
      return true;
    }
    if ( StringUtils.isEmpty( (String) attributes.getAttribute( AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.EXTRA_RAW_CONTENT ) ) == false ) {
      return true;
    }
    if ( StringUtils.isEmpty( (String) attributes.getAttribute( AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT ) ) == false ) {
      return true;
    }
    return false;
  }
}
