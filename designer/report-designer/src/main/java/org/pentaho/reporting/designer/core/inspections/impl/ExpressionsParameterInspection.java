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
import org.pentaho.reporting.designer.core.inspections.AttributeExpressionPropertyLocationInfo;
import org.pentaho.reporting.designer.core.inspections.AttributeLocationInfo;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.inspections.PropertyLocationInfo;
import org.pentaho.reporting.designer.core.inspections.StyleExpressionPropertyLocationInfo;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ValidateableExpression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExpressionsParameterInspection extends AbstractStructureInspection {
  public ExpressionsParameterInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    traverseAttributeExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
    traverseStyleExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
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

        final Object o = utility.getProperty( metaData.getName() );
        if ( metaData.isMandatory() && o == null ) {
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "ExpressionsParameterInspection.MandatoryPropertyMissing",
              expression.getName(), metaData.getDisplayName( Locale.getDefault() ) ),
            new PropertyLocationInfo( expression, metaData.getName() ) ) );
        }
      }

      if ( expression instanceof ValidateableExpression ) {
        final ValidateableExpression vae = (ValidateableExpression) expression;
        final Map map =
          vae.validateParameter( new ReportDesignerDesignTimeContext( designerContext ), Locale.getDefault() );
        final Iterator iterator = map.entrySet().iterator();
        while ( iterator.hasNext() ) {
          final Map.Entry entry = (Map.Entry) iterator.next();
          final String property = (String) entry.getKey();
          final String warning = (String) entry.getValue();
          if ( property == null ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "ExpressionsParameterInspection.ExpressionValidationError",
                expression.getName(), warning ),
              new LocationInfo( expression ) ) );
          } else {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "ExpressionsParameterInspection.ExpressionValidationPropertyError",
                expression.getName(), property, warning ),
              new PropertyLocationInfo( expression, property ) ) );
          }
        }
      }
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }

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
    final String expressionName = expression.getClass().getName();
    if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
      return;
    }

    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionMetaData data = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
      final ExpressionPropertyMetaData[] datas = data.getPropertyDescriptions();
      for ( int i = 0; i < datas.length; i++ ) {
        final ExpressionPropertyMetaData metaData = datas[ i ];
        if ( metaData.isHidden() ) {
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
          continue;
        }
        if ( "name".equals( metaData.getName() ) ) {
          continue;
        }

        final Object o = utility.getProperty( metaData.getName() );
        if ( metaData.isMandatory() && o == null ) {
          final AttributeMetaData attributeMetaData =
            element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
          if ( attributeMetaData == null ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages
                .getString( "ExpressionsParameterInspection.AttributeExpressionMandatoryPropertyMissingNoMetaData",
                  element.getName(), attributeNamespace, attributeName,
                  metaData.getDisplayName( Locale.getDefault() ) ),
              new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                metaData.getName() ) ) );
          } else {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "ExpressionsParameterInspection.AttributeExpressionMandatoryPropertyMissing",
                element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ),
                metaData.getDisplayName( Locale.getDefault() ) ),
              new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                metaData.getName() ) ) );
          }

        }
      }

      if ( expression instanceof ValidateableExpression ) {
        final ValidateableExpression vae = (ValidateableExpression) expression;
        final Map map =
          vae.validateParameter( new ReportDesignerDesignTimeContext( designerContext ), Locale.getDefault() );
        final Iterator iterator = map.entrySet().iterator();
        while ( iterator.hasNext() ) {
          final Map.Entry entry = (Map.Entry) iterator.next();
          final String property = (String) entry.getKey();
          final String warning = (String) entry.getValue();

          final AttributeMetaData attributeMetaData =
            element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
          if ( attributeMetaData == null ) {
            if ( property == null ) {
              resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
                Messages.getString( "ExpressionsParameterInspection.AttributeExpressionValidationErrorNoMetaData",
                  element.getName(), attributeNamespace, attributeName, warning ),
                new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
            } else {
              resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
                Messages
                  .getString( "ExpressionsParameterInspection.AttributeExpressionValidationPropertyErrorNoMetaData",
                    element.getName(), attributeNamespace, attributeName, property, warning ),
                new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName, property ) ) );
            }
          } else {
            if ( property == null ) {
              resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
                Messages.getString( "ExpressionsParameterInspection.AttributeExpressionValidationError",
                  element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ), warning ),
                new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
            } else {
              resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
                Messages.getString( "ExpressionsParameterInspection.AttributeExpressionValidationPropertyError",
                  element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ), property, warning ),
                new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName, property ) ) );
            }
          }
        }
      }
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
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
    final String expressionName = expression.getClass().getName();
    if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
      return;
    }

    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionMetaData data = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
      final ExpressionPropertyMetaData[] datas = data.getPropertyDescriptions();
      for ( int i = 0; i < datas.length; i++ ) {
        final ExpressionPropertyMetaData metaData = datas[ i ];
        if ( metaData.isHidden() ) {
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
          continue;
        }
        if ( "name".equals( metaData.getName() ) ) {
          continue;
        }

        final Object o = utility.getProperty( metaData.getName() );
        if ( metaData.isMandatory() && o == null ) {
          final StyleMetaData description = element.getMetaData().getStyleDescription( styleKey );
          final String displayName;
          if ( description == null ) {
            displayName = styleKey.getName();
          } else {
            displayName = description.getDisplayName( Locale.getDefault() );
          }

          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "ExpressionsParameterInspection.StyleExpressionMandatoryPropertyMissing",
              element.getName(), displayName, metaData.getDisplayName( Locale.getDefault() ) ),
            new StyleExpressionPropertyLocationInfo( element, styleKey, metaData.getName() ) ) );
        }
      }

      if ( expression instanceof ValidateableExpression ) {
        final ValidateableExpression vae = (ValidateableExpression) expression;
        final Map map =
          vae.validateParameter( new ReportDesignerDesignTimeContext( designerContext ), Locale.getDefault() );
        final Iterator iterator = map.entrySet().iterator();
        while ( iterator.hasNext() ) {
          final Map.Entry entry = (Map.Entry) iterator.next();
          final String property = (String) entry.getKey();
          final String warning = (String) entry.getValue();
          if ( property == null ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              warning, new LocationInfo( expression ) ) );
          } else {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              warning, new PropertyLocationInfo( expression, property ) ) );
          }
        }
      }
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }

  }
}
