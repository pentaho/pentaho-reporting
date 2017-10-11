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
import org.pentaho.reporting.designer.core.inspections.ParameterLocationInfo;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;

public class InvalidQueryNameReferenceInspection extends AbstractStructureInspection {
  public InvalidQueryNameReferenceInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
    super.inspect( designerContext, reportRenderContext, resultHandler );
    final AbstractReportDefinition definition = reportRenderContext.getReportDefinition();
    final String query = definition.getQuery();
    if ( query == null ) {
      final AttributeLocationInfo queryLocation = new AttributeLocationInfo
        ( definition, AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, false );
      resultHandler.notifyInspectionResult
        ( new InspectionResult( this, InspectionResult.Severity.HINT,
          Messages.getString( "InvalidQueryNameReferenceInspection.QueryUndefined" ),
          queryLocation ) );

    } else {
      if ( isQueryExecutable( definition, query ) == false ) {
        final AttributeLocationInfo queryLocation = new AttributeLocationInfo
          ( definition, AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, false );
        resultHandler.notifyInspectionResult
          ( new InspectionResult( this, InspectionResult.Severity.ERROR,
            Messages.getString( "InvalidQueryNameReferenceInspection.QueryNotRecognized", query ),
            queryLocation ) );
      }
    }

    final ContextAwareDataSchemaModel dataSchemaModel = reportRenderContext.getReportDataSchemaModel();
    if ( dataSchemaModel.isValid() == false ) {
      final Throwable throwable = dataSchemaModel.getDataFactoryException();
      if ( throwable != null ) {
        final DataFactory dataFactory = reportRenderContext.getContextRoot().getDataFactory();
        final LocationInfo queryLocation;
        if ( dataFactory instanceof CompoundDataFactory ) {
          final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
          final DataFactory element = cdf.getDataFactoryForQuery( query );
          if ( element == null ) {
            queryLocation = new LocationInfo( dataFactory );
          } else {
            queryLocation = new LocationInfo( element );
          }
        } else {
          queryLocation = new LocationInfo( dataFactory );
        }
        resultHandler.notifyInspectionResult
          ( new InspectionResult( this, InspectionResult.Severity.ERROR,
            Messages.getString( "InvalidQueryNameReferenceInspection.QueryDidNotExecute", query, throwable.toString() ),
            queryLocation ) );
      }
    }
  }

  protected void inspectParameter( final ReportDesignerContext designerContext,
                                   final ReportDocumentContext reportRenderContext,
                                   final InspectionResultListener resultHandler,
                                   final String[] columnNames,
                                   final ReportParameterDefinition definition,
                                   final ParameterDefinitionEntry parameter ) {
    if ( parameter instanceof DefaultListParameter ) {
      final DefaultListParameter listParameter = (DefaultListParameter) parameter;
      final String query = listParameter.getQueryName();
      if ( query == null ) {
        final ParameterLocationInfo queryLocation = new ParameterLocationInfo( parameter );
        resultHandler.notifyInspectionResult
          ( new InspectionResult( this, InspectionResult.Severity.HINT,
            Messages.getString( "InvalidQueryNameReferenceInspection.QueryUndefined" ),
            queryLocation ) );

      } else {
        if ( isQueryExecutable( reportRenderContext.getReportDefinition(), query ) == false ) {
          final ParameterLocationInfo queryLocation = new ParameterLocationInfo( parameter );
          resultHandler.notifyInspectionResult
            ( new InspectionResult( this, InspectionResult.Severity.ERROR,
              Messages.getString( "InvalidQueryNameReferenceInspection.QueryNotRecognized", query ),
              queryLocation ) );
        }
      }
    }
  }

  private boolean isQueryExecutable( AbstractReportDefinition definition,
                                     final String query ) {
    while ( definition != null ) {
      if ( definition.getDataFactory().isQueryExecutable( query, new StaticDataRow() ) ) {
        return true;
      }

      final Section parentSection = definition.getParentSection();
      if ( parentSection == null ) {
        definition = null;
      } else {
        definition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
    return false;
  }


}
