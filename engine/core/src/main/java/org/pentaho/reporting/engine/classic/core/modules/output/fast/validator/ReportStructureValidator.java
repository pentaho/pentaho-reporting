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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.LayoutProcessorFunction;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.wizard.RelationalAutoGeneratorPreProcessor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Filter out reports that have any kind of visible style expressions, inline subreport or graphical elements. Also
 * filter reports that utilize any of the formatting functions, except for the row-banding function.
 */
public class ReportStructureValidator extends AbstractStructureVisitor {
  private static final Log logger = LogFactory.getLog( ReportStructureValidator.class );

  private static final List<String> FAST_PROCESSING_UNSUPPORTED_TYPES = Collections.singletonList( "simple-barcodes" );

  private boolean valid;
  private final HashSet<String> preProcessorWhiteList;

  public ReportStructureValidator() {
    preProcessorWhiteList = new HashSet<String>();
    preProcessorWhiteList.add( "org.pentaho.reporting.engine.classic.wizard.WizardProcessor" );
    preProcessorWhiteList.add( RelationalAutoGeneratorPreProcessor.class.getName() );
  }

  public boolean isValidForFastProcessing( MasterReport report ) {
    valid = true;
    inspect( report );
    if ( valid ) {
      logger.info( "Report will execute with fast processing" );
    } else {
      logger.info( "Report will execute with classic processing" );
    }
    return valid;
  }

  private boolean isInlineSubReport( final SubReport reportDefinition ) {
    Section parentSection = reportDefinition.getParentSection();
    if ( !( parentSection instanceof RootLevelBand ) ) {
      return true;
    }

    RootLevelBand rlb = (RootLevelBand) parentSection;
    for ( final SubReport s : rlb.getSubReports() ) {
      if ( s == reportDefinition ) {
        return false;
      }
    }

    return true;
  }

  @Override
  protected void inspectElement( final ReportElement element ) {
    traverseStyleExpressions( element );
    inspectElementType( element );

    if ( element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE ) != null ) {
      valid = false;
      return;
    }

    if ( element.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE ) != null ) {
      valid = false;
      return;
    }

    if ( element.getStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE )
        || element.getStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER )
        || element.getStyleExpression( BandStyleKeys.PAGEBREAK_BEFORE ) != null
        || element.getStyleExpression( BandStyleKeys.PAGEBREAK_AFTER ) != null ) {
      valid = false;
      return;
    }

    if ( element instanceof AbstractReportDefinition ) {
      AbstractReportDefinition report = (AbstractReportDefinition) element;
      for ( ReportPreProcessor reportPreProcessor : report.getPreProcessors() ) {
        if ( preProcessorWhiteList.contains( reportPreProcessor.getClass().getName() ) ) {
          continue;
        }
        valid = false;
        return;
      }

      if ( report instanceof SubReport ) {
        final SubReport sr = (SubReport) report;
        if ( isInlineSubReport( sr ) ) {
          valid = false;
        }
      }
    } else if ( element instanceof CrosstabGroup ) {
      valid = false;
    }
  }

  @Override
  protected void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {
    super.inspectExpression( report, expression );
    if ( expression instanceof RowBandingFunction ) {
      // later we can add code to handle row-banding safely.
      valid = false;
      return;
    }
    if ( expression instanceof LayoutProcessorFunction ) {
      valid = false;
      return;
    }
    if ( expression instanceof PageEventListener && !( expression instanceof PageFunction ) ) {
      valid = false;
    }
  }

  protected void inspectElementType( final ReportElement element ) {
    // If the report has barcodes, the bar codes are not getting rendered in fast processing.
    // This condition has been added to fix the issue reported in PRD-6138
    if ( FAST_PROCESSING_UNSUPPORTED_TYPES.contains( element.getElementType().getMetaData().getName() ) ) {
      valid = false;
    }
  }
}
