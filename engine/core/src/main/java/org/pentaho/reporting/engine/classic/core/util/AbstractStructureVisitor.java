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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractStructureVisitor {
  protected AbstractStructureVisitor() {
  }

  protected void inspect( final AbstractReportDefinition reportDefinition ) {
    if ( reportDefinition instanceof MasterReport ) {
      final MasterReport mr = (MasterReport) reportDefinition;
      final ReportParameterDefinition parameters = mr.getParameterDefinition();
      final ParameterDefinitionEntry[] entries = parameters.getParameterDefinitions();
      for ( int i = 0; i < entries.length; i++ ) {
        final ParameterDefinitionEntry entry = entries[i];
        inspectParameter( reportDefinition, parameters, entry );
      }
    }

    final CompoundDataFactory dataFactory = CompoundDataFactory.normalize( reportDefinition.getDataFactory(), false );
    final int size = dataFactory.size();
    for ( int i = 0; i < size; i++ ) {
      inspectDataSource( reportDefinition, dataFactory.getReference( i ) );
    }

    final ExpressionCollection expressions = reportDefinition.getExpressions();
    final Expression[] expressionsArray = expressions.getExpressions();
    for ( int i = 0; i < expressionsArray.length; i++ ) {
      final Expression expression = expressionsArray[i];
      inspectExpression( reportDefinition, expression );
    }

    inspectElement( reportDefinition );
    traverseSection( reportDefinition );
  }

  protected void traverseSection( final Section section ) {
    traverseSectionWithSubReports( section );
  }

  protected void traverseSectionWithSubReports( final Section section ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof SubReport ) {
        inspect( (SubReport) element );
      } else if ( element instanceof Section ) {
        inspectElement( element );
        traverseSection( (Section) element );

        if ( element instanceof RootLevelBand ) {
          final RootLevelBand rlb = (RootLevelBand) element;
          for ( int sr = 0; sr < rlb.getSubReportCount(); sr += 1 ) {
            inspect( rlb.getSubReport( sr ) );
          }
        }
      } else {
        inspectElement( element );
      }
    }
  }

  protected void traverseSectionWithoutSubReports( final Section section ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof SubReport ) {
        inspectElement( element );
      } else if ( element instanceof Section ) {
        inspectElement( element );
        traverseSection( (Section) element );
      } else {
        inspectElement( element );
      }
    }
  }

  protected void inspectElement( final ReportElement element ) {

  }

  protected void traverseAttributeExpressions( final ReportElement element ) {

    final String[] attrExprNamespaces = element.getAttributeExpressionNamespaces();
    for ( int i = 0; i < attrExprNamespaces.length; i++ ) {
      final String attrExprNamespace = attrExprNamespaces[i];
      final String[] names = element.getAttributeExpressionNames( attrExprNamespace );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final Expression expression = element.getAttributeExpression( attrExprNamespace, name );
        if ( expression == null ) {
          continue;
        }
        final String expressionName = expression.getClass().getName();
        if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
          inspectAttributeExpression( element, attrExprNamespace, name, expression, null );
        } else {
          final ExpressionMetaData metaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
          inspectAttributeExpression( element, attrExprNamespace, name, expression, metaData );
        }
      }
    }
  }

  protected void inspectAttributeExpression( final ReportElement element, final String attributeNamespace,
      final String attributeName, final Expression expression, final ExpressionMetaData expressionMetaData ) {
  }

  protected void traverseStyleExpressions( final ReportElement element ) {

    final Map map = element.getStyleExpressions();
    final Iterator styleExprIt = map.entrySet().iterator();
    while ( styleExprIt.hasNext() ) {
      final Map.Entry entry = (Map.Entry) styleExprIt.next();
      final StyleKey styleKey = (StyleKey) entry.getKey();
      final Expression expression = (Expression) entry.getValue();

      if ( expression == null ) {
        continue;
      }
      final String expressionName = expression.getClass().getName();
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
        inspectStyleExpression( element, styleKey, expression, null );
      } else {
        final ExpressionMetaData metaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
        inspectStyleExpression( element, styleKey, expression, metaData );
      }
    }
  }

  protected void inspectStyleExpression( final ReportElement element, final StyleKey styleKey,
      final Expression expression, final ExpressionMetaData expressionMetaData ) {
  }

  protected void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {

  }

  protected void inspectParameter( final AbstractReportDefinition report, final ReportParameterDefinition definition,
      final ParameterDefinitionEntry parameter ) {
  }

  protected void inspectDataSource( final AbstractReportDefinition report, final DataFactory dataFactory ) {
  }
}
