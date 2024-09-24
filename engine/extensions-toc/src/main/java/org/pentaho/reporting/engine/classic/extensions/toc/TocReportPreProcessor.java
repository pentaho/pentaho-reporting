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

package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;

import java.util.HashSet;

public class TocReportPreProcessor extends AbstractReportPreProcessor {
  private HashSet<String> generatedExpressionNames;

  public TocReportPreProcessor() {
  }

  public MasterReport performPreProcessing( final MasterReport definition,
                                            final DefaultFlowController flowController )
    throws ReportProcessingException {
    try {
      generatedExpressionNames = new HashSet<String>();

      final DataSchema schema = flowController.getDataSchema();
      processSection( schema, definition, definition );
      return definition;
    } finally {
      generatedExpressionNames = null;
    }
  }

  public SubReport performPreProcessing( final SubReport definition,
                                         final DefaultFlowController flowController ) throws ReportProcessingException {
    try {
      generatedExpressionNames = new HashSet<String>();

      final DataSchema schema = flowController.getDataSchema();
      processSection( schema, definition, definition );
      return definition;
    } finally {
      generatedExpressionNames = null;
    }
  }


  private void processSection( final DataSchema schema,
                               final AbstractReportDefinition definition,
                               final Section section ) throws ReportProcessingException {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof SubReport == false ) {
        if ( element instanceof Section ) {
          processSection( schema, definition, (Section) element );
          continue;
        }
        continue;
      }

      if ( "toc".equals( element.getMetaData().getName() ) ) {
        activateTableOfContents( schema, definition, element );
      } else if ( "index".equals( element.getMetaData().getName() ) ) {
        activateIndex( schema, definition, element );
      }

    }
    if ( section instanceof RootLevelBand ) {
      final RootLevelBand rlb = (RootLevelBand) section;
      final SubReport[] reports = rlb.getSubReports();
      for ( int i = 0; i < reports.length; i++ ) {
        final SubReport report = reports[ i ];
        if ( "toc".equals( report.getMetaData().getName() ) ) {
          activateTableOfContents( schema, definition, report );
        } else if ( "index".equals( report.getMetaData().getName() ) ) {
          activateIndex( schema, definition, report );
        }

      }
    }
  }

  private void activateTableOfContents( final DataSchema schema,
                                        final AbstractReportDefinition definition,
                                        final ReportElement element )
    throws ReportProcessingException {
    final TocDataGeneratorFunction o = new TocDataGeneratorFunction();
    final String name = AutoGeneratorUtility.generateUniqueExpressionName
      ( schema, "::report:toc-generator:{0}",
        generatedExpressionNames.toArray( new String[ generatedExpressionNames.size() ] ) );
    o.setName( name );
    generatedExpressionNames.add( name );

    final TocElement toc = (TocElement) element;
    toc.addInputParameter( name, name );
    toc.setQuery( name );

    final Object groupFields = toc.getAttribute( AttributeNames.Core.NAMESPACE, "group-fields" );
    final Object collectDetails = toc.getAttribute( AttributeNames.Core.NAMESPACE, "collect-details" );
    final Object indexSeparator = toc.getAttribute( AttributeNames.Core.NAMESPACE, "index-separator" );
    final Object titleFormula = toc.getAttribute( AttributeNames.Core.NAMESPACE, "title-formula" );
    final Object titleField = toc.getAttribute( AttributeNames.Core.NAMESPACE, "title-field" );

    if ( Boolean.TRUE.equals( collectDetails ) ) {
      o.setCollectDetails( true );
    } else if ( Boolean.FALSE.equals( collectDetails ) ) {
      o.setCollectDetails( false );
    }
    if ( groupFields instanceof String[] ) {
      final String[] fields = (String[]) groupFields;
      o.setGroup( fields );
    }
    if ( indexSeparator != null ) {
      o.setIndexSeparator( String.valueOf( indexSeparator ) );
    }
    if ( titleFormula != null ) {
      o.setTitleFormula( String.valueOf( titleFormula ) );
    }
    if ( titleField != null ) {
      o.setTitleField( String.valueOf( titleField ) );
    }

    final DataFactory dataFactory = toc.getDataFactory();
    final CompoundDataFactory normalizedDataFactory = CompoundDataFactory.normalize( dataFactory );
    normalizedDataFactory.add( 0, new DataPassingDataFactory( name ) );
    toc.setDataFactory( normalizedDataFactory );

    definition.addExpression( o );
  }


  private void activateIndex( final DataSchema schema,
                              final AbstractReportDefinition definition,
                              final ReportElement element )
    throws ReportProcessingException {
    final IndexDataGeneratorFunction o = new IndexDataGeneratorFunction();
    final String name = AutoGeneratorUtility.generateUniqueExpressionName
      ( schema, "::report:index-generator:{0}",
        generatedExpressionNames.toArray( new String[ generatedExpressionNames.size() ] ) );
    o.setName( name );
    generatedExpressionNames.add( name );

    final IndexElement toc = (IndexElement) element;
    toc.addInputParameter( name, name );
    toc.setQuery( name );

    final Object indexSeparator = toc.getAttribute( AttributeNames.Core.NAMESPACE, "index-separator" );
    final Object titleFormula = toc.getAttribute( AttributeNames.Core.NAMESPACE, "data-formula" );
    final Object titleField = toc.getAttribute( AttributeNames.Core.NAMESPACE, "data-field" );
    final Object condensedStyle = toc.getAttribute( AttributeNames.Core.NAMESPACE, "condensed-style" );

    if ( indexSeparator != null ) {
      o.setIndexSeparator( String.valueOf( indexSeparator ) );
    }
    if ( titleFormula != null ) {
      o.setDataFormula( String.valueOf( titleFormula ) );
    }
    if ( titleField != null ) {
      o.setDataField( String.valueOf( titleField ) );
    }
    if ( Boolean.TRUE.equals( condensedStyle ) ) {
      o.setCondensedStyle( true );
    } else if ( Boolean.FALSE.equals( condensedStyle ) ) {
      o.setCondensedStyle( false );
    }

    final DataFactory dataFactory = toc.getDataFactory();
    final CompoundDataFactory normalizedDataFactory = CompoundDataFactory.normalize( dataFactory );
    normalizedDataFactory.add( 0, new DataPassingDataFactory( name ) );
    toc.setDataFactory( normalizedDataFactory );

    definition.addExpression( o );
  }
}
