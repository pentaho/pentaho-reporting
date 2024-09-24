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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverter;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverterRegistry;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;

public class DefaultLayoutBuilderStrategy implements LayoutBuilderStrategy {
  private static final Log logger = LogFactory.getLog( DefaultLayoutBuilderStrategy.class );

  private ExpressionRuntime runtime;
  private ArrayList<InlineSubreportMarker> collectedReports;
  private boolean designtime;
  private final RichTextStyleResolver styleResolver;

  public DefaultLayoutBuilderStrategy( RichTextStyleResolver styleResolver ) {
    ArgumentNullException.validate( "styleResolver", styleResolver );
    this.styleResolver = styleResolver;
    collectedReports = new ArrayList<InlineSubreportMarker>();
  }

  public void add( final ExpressionRuntime runtime, final LayoutModelBuilder builder, final Band band,
      final List<InlineSubreportMarker> collectedSubReports ) throws ReportProcessingException {
    if ( runtime == null ) {
      throw new NullPointerException();
    }
    if ( builder == null ) {
      throw new NullPointerException();
    }
    if ( band == null ) {
      throw new NullPointerException();
    }

    try {
      this.runtime = runtime;
      final OutputProcessorMetaData outputProcessorMetaData =
          runtime.getProcessingContext().getOutputProcessorMetaData();
      this.designtime = outputProcessorMetaData.isFeatureSupported( OutputProcessorFeature.DESIGNTIME );
      collectedReports.clear();

      final SimpleStyleSheet styleSheet = band.getComputedStyle();
      final boolean invConsSpace = builder.isEmptyElementsHaveSignificance();
      if ( invConsSpace || isElementProcessable( band, styleSheet ) ) {
        if ( addBandInternal( band, builder, true ) ) {
          // when empty, add a progress marker box
          builder.addProgressMarkerBox();
        }
      } else {
        // element is not processable
        builder.addProgressMarkerBox();
      }
    } finally {
      this.runtime = null;
    }

    collectedSubReports.addAll( this.collectedReports );
  }

  private boolean addBandInternal( final Section band, final LayoutModelBuilder builder, final boolean root ) {
    builder.startBox( band );
    final boolean invConsSpace = builder.isEmptyElementsHaveSignificance();
    for ( final ReportElement element : band ) {
      final StyleSheet styleSheet = element.getComputedStyle();
      if ( invConsSpace == false ) {
        // pre-prune the layout model ...
        if ( isElementProcessable( element, styleSheet ) == false ) {
          continue;
        }
      }

      if ( element instanceof SubReport ) {
        processSubReport( (SubReport) element, builder );
        continue;
      }

      if ( element instanceof Section ) {
        addBandInternal( (Section) element, builder, false );
      } else {
        processContent( element, builder );
      }
    }

    if ( root == false && builder.isEmpty() ) {
      final OutputProcessorMetaData metaData = runtime.getProcessingContext().getOutputProcessorMetaData();
      if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
        // this is the behaviour of the old 3.9 code. It is hard to explain in sane words, just look at
        // the old LayoutBuilder class for an example.
        final StyleSheet computedStyle = band.getComputedStyle();
        if ( invConsSpace == false && DefaultLayoutModelBuilder.isControlBand( computedStyle ) == false ) {
          // creates either a block or a inline element
          if ( band.getElementCount() > 0 ) {
            builder.legacyFlagNotEmpty();
          }
          builder.finishBox();
          builder.legacyAddPlaceholder( band );
          return false;
        }

        builder.legacyFlagNotEmpty();
      }
    }

    return builder.finishBox();
  }

  protected void processSubReport( final SubReport subReport, final LayoutModelBuilder builder ) {
    final InlineSubreportMarker marker = builder.processSubReport( subReport );
    if ( marker != null ) {
      logger.debug( "Process Subreport: " + marker.getInsertationPointId() );
      collectedReports.add( marker );
    } else {
      logger.debug( "Process Subreport: NOT returning anything." );

    }
  }

  protected Object filterRichText( final ReportElement element, final Object initialValue ) {
    final Object richTextType =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE );
    if ( richTextType != null ) {
      final RichTextConverterRegistry registry = RichTextConverterRegistry.getRegistry();
      final RichTextConverter converter = registry.getConverter( String.valueOf( richTextType ) );
      if ( converter != null ) {
        return converter.convert( element, initialValue );
      }
    }
    return initialValue;
  }

  protected void processContent( final ReportElement element, final LayoutModelBuilder builder ) {
    final Object value = filterRichText( element, computeValue( runtime, element ) );
    if ( value == null ) {
      builder.processContent( element, null, null );
      return;
    }

    if ( value instanceof Section ) {
      final Section section = (Section) value;
      styleResolver.resolveRichTextStyle( section );
      addBandInternal( section, builder, false );
      return;
    }

    final DataSource dataSource = element.getElementType();
    final Object rawValue;
    if ( dataSource instanceof RawDataSource ) {
      final RawDataSource rds = (RawDataSource) dataSource;
      rawValue = rds.getRawValue( runtime, element );
    } else {
      rawValue = null;
    }

    builder.processContent( element, value, rawValue );
  }

  protected boolean isElementProcessable( final ReportElement element, final StyleSheet style ) {
    if ( designtime ) {
      final Object attribute =
          element.getAttribute( AttributeNames.Designtime.NAMESPACE,
              AttributeNames.Designtime.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
      if ( Boolean.TRUE.equals( attribute ) ) {
        return false;
      }
      return true;
    }

    return style.getBooleanStyleProperty( ElementStyleKeys.VISIBLE );
  }

  protected Object computeValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( designtime ) {
      final Object value = element.getElementType().getDesignValue( runtime, element );
      // should be ok for most cases ..
      if ( value != null ) {
        return value;
      }

      return element.getElementType().getMetaData().getName();
    }

    return element.getElementType().getValue( runtime, element );
  }

}
