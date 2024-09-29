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


package org.pentaho.reporting.designer.core.inspections.impl;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.StyleLocationInfo;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * Checks, whether all mandatory element attributes are set. This also checks whether all fieldnames are set.
 *
 * @author Thomas Morgner
 */
public class FontSizeInspection extends AbstractStructureInspection {
  private OutputProcessorMetaData outputProcessorMetaData;

  public FontSizeInspection() {
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
    outputProcessorMetaData = new GenericOutputProcessorMetaData();
    super.inspect( designerContext, reportRenderContext, resultHandler );
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    final boolean dynHeight = element.getStyle().getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT );
    if ( dynHeight ) {
      return;
    }
    if ( element instanceof Section ) {
      return;
    }
    final ElementMetaData metaData = element.getMetaData();
    if ( metaData.isContainerElement() ) {
      return;
    }

    if ( isTextElement( metaData ) == false ) {
      return;
    }

    final int minHeight = element.getStyle().getIntStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 );
    final double lineHeight = element.getStyle().getIntStyleProperty( TextStyleKeys.LINEHEIGHT, 0 );
    final int declaredFontSize = element.getStyle().getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );

    final FontMetrics fontMetrics = outputProcessorMetaData.getFontMetrics( element.getStyle() );
    final double fontHeight = StrictGeomUtility.toExternalValue( RenderableText.convert( fontMetrics.getMaxHeight() ) );

    final long effectiveLineHeight =
      RenderLength.resolveLength( StrictGeomUtility.toInternalValue( declaredFontSize ), lineHeight );
    if ( fontHeight <= Math.max( minHeight, StrictGeomUtility.toExternalValue( effectiveLineHeight ) ) ) {
      return;
    }

    resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
      Messages.getString( "FontSizeInspection.ElementHeightSmallerThanLineSize", element.getName() ),
      new StyleLocationInfo( element, ElementStyleKeys.MIN_HEIGHT, false ) ) );

  }

  private boolean isTextElement( final ElementMetaData metaData ) {
    final Class aClass = metaData.getContentType();
    if ( String.class == aClass ) {
      return true;
    }
    return "legacy-element".equals( metaData.getName() );//NON-NLS
  }
}
