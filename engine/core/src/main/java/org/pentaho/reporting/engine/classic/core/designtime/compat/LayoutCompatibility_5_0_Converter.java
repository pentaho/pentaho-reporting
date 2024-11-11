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


package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;

public class LayoutCompatibility_5_0_Converter extends AbstractCompatibilityConverter {
  private SimpleStyleResolver styleResolver;
  private ResolverStyleSheet resolverStyleSheet;

  public LayoutCompatibility_5_0_Converter() {
    this.styleResolver = new SimpleStyleResolver( true );
    this.resolverStyleSheet = new ResolverStyleSheet();
  }

  public int getTargetVersion() {
    return ClassicEngineBoot.computeVersionId( 5, 0, 0 );
  }

  private boolean isBlockLevelBox( final Section s ) {
    if ( s == null ) {
      return true;
    }

    styleResolver.resolve( s, resolverStyleSheet );
    final Object layout = resolverStyleSheet.getStyleProperty( BandStyleKeys.LAYOUT );
    if ( BandStyleKeys.LAYOUT_BLOCK.equals( layout ) ) {
      return true;
    }
    if ( layout == null || BandStyleKeys.LAYOUT_AUTO.equals( layout ) ) {
      return isBlockLevelBox( s.getParent() );
    }
    return false;
  }

  public void inspectElement( final ReportElement element ) {
    if ( element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.CONTROL ) {
      element.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, null );
      element.setStyleExpression( BandStyleKeys.LAYOUT, null );
    }

    if ( element instanceof CrosstabGroup ) {
      // legacy crosstab reports did not have a notion of details headers, so they would be empty.
      // make sure that they do not appear by default.
      final CrosstabGroup g = (CrosstabGroup) element;
      g.setPrintDetailsHeader( false );
    }

    if ( element instanceof AbstractReportDefinition || element instanceof Group || element instanceof GroupBody ) {
      // users may have set all sorts of random values. We need to auto-correct that.
      element.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, null );
    }

    final Section parentSection = element.getParentSection();
    if ( isBlockLevelBox( parentSection ) ) {
      if ( element instanceof SubReport && parentSection instanceof RootLevelBand ) {
        final SubReport subReport = (SubReport) element;
        final RootLevelBand rootLevelBand = (RootLevelBand) parentSection;
        for ( final SubReport r : rootLevelBand.getSubReports() ) {
          if ( r == subReport ) {
            // In the old days, banded-subreports did not receive any style when computing the layout.
            // When migrating report, we at least remove the most offensive properties on subreports.
            subReport.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, null );
            subReport.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, null );
          }
        }
      }
    }
  }
}
