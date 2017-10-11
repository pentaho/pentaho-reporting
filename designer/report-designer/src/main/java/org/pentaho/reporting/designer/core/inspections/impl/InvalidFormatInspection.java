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
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class InvalidFormatInspection extends AbstractStructureInspection {
  public InvalidFormatInspection() {
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
      if ( value instanceof String == false ) {
        continue;
      }

      final String fmtString = (String) value;
      final String role = data.getValueRole();
      try {
        if ( "NumberFormat".equals( role ) )//NON-NLS
        {
          final DecimalFormat fmt = new DecimalFormat( fmtString );
        } else if ( "DateFormat".equals( role ) )//NON-NLS
        {
          //noinspection SimpleDateFormatWithoutLocale
          final DateFormat fmt = new SimpleDateFormat( fmtString );
        } else if ( "Message".equals( role ) )//NON-NLS
        {
          final MessageFormatSupport support = new MessageFormatSupport();
          support.setFormatString( fmtString );
        }
      } catch ( Exception e ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "InvalidFormatInspection.AttributeInvalidFormat",
            element.getName(), data.getDisplayName( Locale.getDefault() ), fmtString ),
          new AttributeLocationInfo( element, data.getNameSpace(), data.getName(), false ) ) );
      }

    }

    traverseAttributeExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
    traverseStyleExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
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
        if ( o instanceof String == false ) {
          continue;
        }
        final String fmtString = (String) o;
        final String role = metaData.getPropertyRole();
        try {
          if ( "NumberFormat".equals( role ) )//NON-NLS
          {
            final DecimalFormat fmt = new DecimalFormat( String.valueOf( o ) );
          } else if ( "DateFormat".equals( role ) )//NON-NLS
          {
            //noinspection SimpleDateFormatWithoutLocale
            final DateFormat fmt = new SimpleDateFormat( String.valueOf( o ) );
          } else if ( "Message".equals( role ) )//NON-NLS
          {
            final MessageFormatSupport support = new MessageFormatSupport();
            support.setFormatString( fmtString );
          }
        } catch ( Exception e ) {
          final AttributeMetaData attrMetaData =
            element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
          if ( attrMetaData == null ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidFormatInspection.AttributeExpressionInvalidFormatNoMetaData",
                element.getName(), attributeNamespace, attributeName, fmtString,
                metaData.getDisplayName( Locale.getDefault() ) ),
              new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                metaData.getName() ) ) );
          } else {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidFormatInspection.AttributeExpressionInvalidFormat",
                element.getName(), attrMetaData.getDisplayName( Locale.getDefault() ), fmtString,
                metaData.getDisplayName( Locale.getDefault() ) ),
              new AttributeExpressionPropertyLocationInfo( element, attributeNamespace, attributeName,
                metaData.getName() ) ) );
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
    if ( expressionMetaData == null ) {
      return;
    }

    try {
      final BeanUtility utility = new BeanUtility( expression );
      final ExpressionPropertyMetaData[] propertyDescriptions = expressionMetaData.getPropertyDescriptions();
      for ( int x = 0; x < propertyDescriptions.length; x++ ) {
        final ExpressionPropertyMetaData metaData = propertyDescriptions[ x ];
        final Object o = utility.getProperty( metaData.getName() );
        if ( o instanceof String == false ) {
          continue;
        }
        final String fmtString = (String) o;
        final String role = metaData.getPropertyRole();
        try {
          if ( "NumberFormat".equals( role ) )//NON-NLS
          {
            final DecimalFormat fmt = new DecimalFormat( String.valueOf( o ) );
          } else if ( "DateFormat".equals( role ) )//NON-NLS
          {
            //noinspection SimpleDateFormatWithoutLocale
            final DateFormat fmt = new SimpleDateFormat( String.valueOf( o ) );
          } else if ( "Message".equals( role ) )//NON-NLS
          {
            final MessageFormatSupport support = new MessageFormatSupport();
            support.setFormatString( fmtString );
          }
        } catch ( Exception e ) {
          final StyleMetaData attrMetaData = element.getMetaData().getStyleDescription( styleKey );
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "InvalidFormatInspection.StyleExpressionInvalidFormat",
              element.getName(), attrMetaData.getDisplayName( Locale.getDefault() ), fmtString,
              metaData.getDisplayName( Locale.getDefault() ) ),
            new StyleExpressionPropertyLocationInfo( element, styleKey, metaData.getName() ) ) );
        }
      }
    } catch ( Exception e ) {
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
        if ( o instanceof String == false ) {
          continue;
        }
        final String fmtString = (String) o;
        final String role = metaData.getPropertyRole();
        try {
          if ( "NumberFormat".equals( role ) )//NON-NLS
          {
            final DecimalFormat fmt = new DecimalFormat( String.valueOf( o ) );
          } else if ( "DateFormat".equals( role ) )//NON-NLS
          {
            //noinspection SimpleDateFormatWithoutLocale
            final DateFormat fmt = new SimpleDateFormat( String.valueOf( o ) );
          } else if ( "Message".equals( role ) )//NON-NLS
          {
            final MessageFormatSupport support = new MessageFormatSupport();
            support.setFormatString( fmtString );
          }
        } catch ( Exception e ) {
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "InvalidFormatInspection.ExpressionInvalidFormat",
              expression.getName(), metaData.getDisplayName( Locale.getDefault() ), fmtString,
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
