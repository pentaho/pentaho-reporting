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
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import java.util.Locale;

public class InvalidGroupReferenceInspection extends AbstractStructureInspection {
  public InvalidGroupReferenceInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    final AttributeMetaData[] datas = element.getMetaData().getAttributeDescriptions();
    for ( int i = 0; i < datas.length; i++ ) {
      final AttributeMetaData metaData = datas[ i ];

      if ( !"Group".equals( metaData.getValueRole() ) )//NON-NLS
      {
        continue;
      }

      final Object value = element.getAttribute( metaData.getNameSpace(), metaData.getName() );
      final String[] groups = metaData.getReferencedGroups( element, value );
      for ( int j = 0; j < groups.length; j++ ) {
        final String group = groups[ j ];
        final AbstractReportDefinition reportDefinition = reportRenderContext.getReportDefinition();
        final ReportElement e = reportDefinition.getGroupByName( group );
        if ( e == null ) {
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "InvalidGroupReferenceInspection.AttributeInvalidGroup",
              element.getName(), group, metaData.getDisplayName( Locale.getDefault() ) ),
            new AttributeLocationInfo( element, metaData.getNameSpace(), metaData.getName(), false ) ) );
        }
      }
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
        if ( metaData.isHidden() ) {
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
          continue;
        }

        if ( !"Group".equals( metaData.getPropertyRole() ) )//NON-NLS
        {
          continue;
        }

        final Object o = utility.getProperty( metaData.getName() );
        final String[] elements = metaData.getReferencedGroups( expression, o );
        for ( int j = 0; j < elements.length; j++ ) {
          final String element = elements[ j ];
          final AbstractReportDefinition reportDefinition = reportRenderContext.getReportDefinition();
          final ReportElement e = reportDefinition.getGroupByName( element );
          if ( e == null ) {
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "InvalidGroupReferenceInspection.ExpressionInvalidGroup",
                expression.getName(), element, metaData.getDisplayName( Locale.getDefault() ) ),
              new PropertyLocationInfo( expression, metaData.getName() ) ) );
          }
        }
      }
    } catch ( Exception e ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        e.getMessage(), new LocationInfo( expression ) ) );
    }

  }
}
