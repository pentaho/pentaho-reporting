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
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Locale;

import static org.pentaho.reporting.designer.core.inspections.InspectionResult.Severity;

public class InvalidFieldReferenceInspection extends AbstractStructureInspection {
  public InvalidFieldReferenceInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    final AttributeMetaData[] datas = element.getMetaData().getAttributeDescriptions();
    for ( int i = 0; i < datas.length; i++ ) {
      final AttributeMetaData data = datas[ i ];
      final Object value = element.getAttribute( data.getNameSpace(), data.getName() );
      final String[] referencedFields = data.getReferencedFields( element, value );

      for ( int j = 0; j < referencedFields.length; j++ ) {
        final String field = referencedFields[ j ];
        if ( isValidField( field, columnNames ) == false ) {
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "InvalidFieldReferenceInspection.AttributeInvalidField", element.getName(),
              field, data.getDisplayName( Locale.getDefault() ) ),
            new AttributeLocationInfo( element, data.getNameSpace(), data.getName(), false )
          ) );
        }
      }
    }

    traverseAttributeExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
    traverseStyleExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );

    if ( element instanceof SubReport && element != reportRenderContext.getReportDefinition() ) {
      final SubReport report = (SubReport) element;
      final ParameterMapping[] parameterMappings = report.getInputMappings();
      for ( int i = 0; i < parameterMappings.length; i++ ) {
        final ParameterMapping mapping = parameterMappings[ i ];
        if ( "*".equals( mapping.getName() ) ) {
          continue;
        }

        if ( isValidField( mapping.getName(), columnNames ) == false ) {
          String message = Messages.getString( "InvalidFieldReferenceInspection.SubReportInvalidField",
            report.getName(), mapping.getName() );
          InspectionResult ir = new InspectionResult( this, Severity.WARNING, message, new LocationInfo( report ) );
          resultHandler.notifyInspectionResult( ir );
        }
      }
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

    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionPropertyMetaData[] propertyDescriptions = expressionMetaData.getPropertyDescriptions();
      for ( int x = 0; x < propertyDescriptions.length; x++ ) {
        final ExpressionPropertyMetaData metaData = propertyDescriptions[ x ];
        final Object o = utility.getProperty( metaData.getName() );
        final String[] referencedFields = metaData.getReferencedFields( expression, o );
        for ( int y = 0; y < referencedFields.length; y++ ) {
          final String field = referencedFields[ y ];
          if ( isValidField( field, columnNames ) == false ) {
            final AttributeMetaData attrMetaData =
              element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
            if ( attrMetaData == null ) {
              resultHandler.notifyInspectionResult( new InspectionResult( this, Severity.WARNING,
                Messages.getString( "InvalidFieldReferenceInspection.AttributeExpressionInvalidFieldNoMetaData",
                  element.getName(), attributeNamespace, attributeName, field,
                  metaData.getDisplayName( Locale.getDefault() ) ),
                new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                  metaData.getName() )
              ) );
            } else {
              resultHandler.notifyInspectionResult( new InspectionResult( this, Severity.WARNING,
                Messages.getString( "InvalidFieldReferenceInspection.AttributeExpressionInvalidField",
                  element.getName(), attrMetaData.getDisplayName( Locale.getDefault() ), field,
                  metaData.getDisplayName( Locale.getDefault() ) ),
                new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                  metaData.getName() )
              ) );
            }
          }
        }
      }
    } catch ( final Exception e ) {
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
    if ( expressionMetaData == null ) {
      return;
    }

    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionPropertyMetaData[] propertyDescriptions = expressionMetaData.getPropertyDescriptions();
      for ( int x = 0; x < propertyDescriptions.length; x++ ) {
        final ExpressionPropertyMetaData metaData = propertyDescriptions[ x ];
        final Object o = utility.getProperty( metaData.getName() );
        final String[] referencedFields = metaData.getReferencedFields( expression, o );
        for ( int y = 0; y < referencedFields.length; y++ ) {
          final String field = referencedFields[ y ];
          if ( isValidField( field, columnNames ) == false ) {
            final StyleMetaData styleDescription = element.getMetaData().getStyleDescription( styleKey );
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidFieldReferenceInspection.StyleExpressionInvalidField",
                element.getName(), styleDescription.getDisplayName( Locale.getDefault() ),
                field, metaData.getDisplayName( Locale.getDefault() ) ),
              new StyleExpressionPropertyLocationInfo( element, styleKey, metaData.getName() )
            ) );
          }
        }
      }
    } catch ( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
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
        final Object o = utility.getProperty( metaData.getName() );
        final String[] referencedFields = metaData.getReferencedFields( expression, o );
        for ( int j = 0; j < referencedFields.length; j++ ) {
          final String field = referencedFields[ j ];
          if ( isValidField( field, columnNames ) == false ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidFieldReferenceInspection.ExpressionInvalidField", expression.getName(),
                field, metaData.getDisplayName( Locale.getDefault() ) ),
              new PropertyLocationInfo( expression, metaData.getName() )
            ) );
          }
        }
      }
    } catch ( final Exception e ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        e.getMessage(),
        new LocationInfo( expression ) ) );
    }

  }

  private boolean isValidField( final String field, final String[] colnames ) {
    for ( int i = 0; i < colnames.length; i++ ) {
      if ( ObjectUtilities.equal( colnames[ i ], field ) ) {
        return true;
      }

    }
    return false;
  }

  protected void inspectDataSource( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final DataFactory dataFactory ) {
    final StaticDataRow dataRow = new StaticDataRow( columnNames, new Object[ columnNames.length ] );

    final String[] queries = dataFactory.getQueryNames();
    for ( int i = 0; i < queries.length; i++ ) {
      final String query = queries[ i ];
      if ( DesignTimeUtil.isSelectedDataSource( reportRenderContext.getReportDefinition(), dataFactory, query ) ) {
        final DataFactoryMetaData metaData = dataFactory.getMetaData();
        final String[] referencedFields = metaData.getReferencedFields( dataFactory, query, dataRow );
        if ( referencedFields == null ) {
          continue;
        }
        for ( int j = 0; j < referencedFields.length; j++ ) {
          final String field = referencedFields[ j ];
          if ( isValidField( field, columnNames ) == false ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidFieldReferenceInspection.DataSourceInvalidField",
                metaData.getDisplayName( Locale.getDefault() ), field, query ),
              new PropertyLocationInfo( dataFactory, query )
            ) );
          }
        }
      }
    }
  }
}
