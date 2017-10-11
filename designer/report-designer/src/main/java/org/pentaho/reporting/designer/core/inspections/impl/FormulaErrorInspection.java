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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.inspections.impl;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.inspections.AttributeLocationInfo;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.inspections.PropertyLocationInfo;
import org.pentaho.reporting.designer.core.inspections.StyleLocationInfo;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

import java.util.Locale;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class FormulaErrorInspection extends AbstractStructureInspection {
  public FormulaErrorInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectAttributeExpression( final ReportDesignerContext designerContext,
                                             final ReportDocumentContext reportRenderContext,
                                             final InspectionResultListener resultHandler,
                                             final String[] columnNames,
                                             final ReportElement element,
                                             final String attributeNamespace,
                                             final String attributeName,
                                             final Expression expression,
                                             final ExpressionMetaData expressionMetaData ) {
    if ( !( expression instanceof FormulaExpression ) ) {
      return;
    }

    final FormulaExpression fe = (FormulaExpression) expression;
    final String s = fe.getFormula();
    if ( StringUtils.isEmpty( s, true ) ) {
      final AttributeMetaData attrMeta =
        element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
      if ( attrMeta != null ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.AttributeNoFormulaNoMetaData",
            element.getName(), attrMeta.getDisplayName( Locale.getDefault() ) ),
          new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
      } else {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.AttributeNoFormulaNoMetaData",
            element.getName(), attributeNamespace, attributeName ),
          new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
      }
      return;
    }

    try {
      compileFormula( s );
    } catch ( ParseException pe ) {

      final AttributeMetaData attrMeta = element.getMetaData().getAttributeDescription
        ( attributeNamespace, attributeName );
      if ( attrMeta != null ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.AttributeInvalidFormula",
            element.getName(), attrMeta.getDisplayName( Locale.getDefault() ) ),
          new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
      } else {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.AttributeInvalidFormulaNoMetaData",
            element.getName(), attributeNamespace, attributeName ),
          new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
      }
    }
  }

  protected void inspectStyleExpression( final ReportDesignerContext designerContext,
                                         final ReportDocumentContext reportRenderContext,
                                         final InspectionResultListener resultHandler,
                                         final String[] columnNames,
                                         final ReportElement element,
                                         final StyleKey styleKey,
                                         final Expression expression,
                                         final ExpressionMetaData expressionMetaData ) {

    if ( expression instanceof FormulaExpression == false ) {
      return;
    }
    final FormulaExpression fe = (FormulaExpression) expression;
    final String s = fe.getFormula();
    if ( StringUtils.isEmpty( s, true ) ) {
      final StyleMetaData description = element.getMetaData().getStyleDescription( styleKey );
      if ( description == null ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.StyleNoFormulaNoMetaData",
            element.getName(), styleKey.getName() ),
          new StyleLocationInfo( element, styleKey, true ) ) );
      } else {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.StyleNoFormula",
            element.getName(), description.getDisplayName( Locale.getDefault() ) ),
          new StyleLocationInfo( element, styleKey, true ) ) );
      }
      return;
    }

    try {
      compileFormula( s );
    } catch ( ParseException pe ) {
      // pe is ignored
      final StyleMetaData description = element.getMetaData().getStyleDescription( styleKey );
      if ( description != null ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.StyleInvalidFormula",
            element.getName(), description.getDisplayName( Locale.getDefault() ) ),
          new StyleLocationInfo( element, styleKey, true ) ) );
      } else {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "FormulaErrorInspection.StyleInvalidFormulaNoMetaData",
            element.getName(), styleKey.getName() ),
          new StyleLocationInfo( element, styleKey, true ) ) );
      }
    }
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    traverseAttributeExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
    traverseStyleExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
  }

  private Formula compileFormula( final String formula ) throws ParseException {
    // Namespace is not yet used.
    // final String formulaNamespace;
    final String formulaExpression;
    if ( formula == null ) {
      throw new ParseException( "Formula is invalid" );
    }

    if ( formula.length() > 0 && formula.charAt( 0 ) == '=' ) {
      //      formulaNamespace = "report";
      formulaExpression = formula.substring( 1 );
    } else {
      final int separator = formula.indexOf( ':' );
      if ( separator <= 0 || ( ( separator + 1 ) == formula.length() ) ) {
        // error: invalid formula.
        //        formulaNamespace = null;
        formulaExpression = null;
      } else {
        //        formulaNamespace = formula.substring(0, separator);
        formulaExpression = formula.substring( separator + 1 );
      }
    }

    if ( formulaExpression == null ) {
      throw new ParseException( "Formula is invalid" );
    }
    return new Formula( formulaExpression );
  }

  protected void inspectExpression( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final Expression expression,
                                    final ExpressionMetaData expressionMetaData ) {
    if ( expressionMetaData == null ) {
      return;
    }


    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionPropertyMetaData[] datas = expressionMetaData.getPropertyDescriptions();
      for ( int i = 0; i < datas.length; i++ ) {
        final ExpressionPropertyMetaData metaData = datas[ i ];
        if ( metaData.isHidden() ) {
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
          continue;
        }

        if ( MetaData.VALUEROLE_FORMULA.equals( metaData.getPropertyRole() ) == false )//NON-NLS
        {
          continue;
        }

        final Object o = utility.getProperty( metaData.getName() );
        if ( o instanceof String == false ) {
          continue;
        }

        try {
          compileFormula( (String) o );
        } catch ( ParseException fpe ) {
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "FormulaErrorInspection.ExpressionInvalidFormula", expression.getName(),
              metaData.getDisplayName( Locale.getDefault() ) ),
            new PropertyLocationInfo( expression, metaData.getName() ) ) );
        }
      }
    } catch ( Exception e ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        e.getMessage(),
        new LocationInfo( expression ) ) );
    }
  }
}
