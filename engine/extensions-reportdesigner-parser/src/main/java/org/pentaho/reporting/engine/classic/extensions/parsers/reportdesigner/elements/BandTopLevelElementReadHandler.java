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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.model.Guideline;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report.LinealModelReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report.RowBandingDefinitionReadHandler;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This unifies all top-level band types.
 *
 * @author Thomas Morgner
 */
public class BandTopLevelElementReadHandler extends BandElementReadHandler {
  private Band rootLevelBand;
  private LinealModelReadHandler verticalLinealModel;
  private RowBandingDefinitionReadHandler rowBandingDefinitionReadHandler;
  private String bandType;
  private ArrayList subreports;

  public BandTopLevelElementReadHandler( final Band rootLevelBand,
                                         final String bandType ) {
    super( rootLevelBand );
    this.rootLevelBand = rootLevelBand;
    this.bandType = bandType;
    this.subreports = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "verticalLinealModel".equals( tagName ) ) {
        verticalLinealModel = new LinealModelReadHandler();
        return verticalLinealModel;
      }
      if ( "ITEM_BAND".equals( bandType ) && "rowBandingDefinition".equals( tagName ) ) {
        rowBandingDefinitionReadHandler = new RowBandingDefinitionReadHandler();
        return rowBandingDefinitionReadHandler;
      }
      if ( "child".equalsIgnoreCase( tagName ) ) {
        final String type = atts.getValue( uri, "type" );
        if ( "org.pentaho.reportdesigner.crm.report.model.SubReportElement".equals( type ) ) {
          final SubreportElementReadHandler readHandler = new SubreportElementReadHandler();
          subreports.add( readHandler );
          return readHandler;
        }
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties result = getResult();

    final String displayOnLastPage = result.getProperty( "displayOnLastPage" );
    if ( displayOnLastPage != null ) {
      if ( "true".equals( displayOnLastPage ) ) {
        getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, Boolean.FALSE );
      }
    }

    final String displayOnFirstPage = result.getProperty( "displayOnFirstPage" );
    if ( displayOnFirstPage != null ) {
      if ( "true".equals( displayOnFirstPage ) ) {
        getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, Boolean.FALSE );
      }
    }

    final String repeat = result.getProperty( "repeat" );
    if ( repeat != null ) {
      if ( "true".equals( repeat ) ) {
        getStyle().setStyleProperty( BandStyleKeys.REPEAT_HEADER, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.REPEAT_HEADER, Boolean.FALSE );
      }
    }

    final String sticky = result.getProperty( "sticky" );
    if ( sticky != null ) {
      if ( "true".equals( sticky ) ) {
        getStyle().setStyleProperty( BandStyleKeys.STICKY, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.STICKY, Boolean.FALSE );
      }
    }

    final String pageBreakBefore = result.getProperty( "pageBreakBefore" );
    if ( pageBreakBefore != null ) {
      if ( "true".equals( pageBreakBefore ) ) {
        getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, Boolean.FALSE );
      }
    }

    final String pageBreakAfter = result.getProperty( "pageBreakAfter" );
    if ( pageBreakAfter != null ) {
      if ( "true".equals( pageBreakAfter ) ) {
        getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, Boolean.FALSE );
      }
    }

    final String visualHeight = result.getProperty( "visualHeight" );
    final float visualHeightParsed = ParserUtil.parseFloat( visualHeight, 0 );
    if ( visualHeightParsed > 0 ) {
      rootLevelBand.setAttribute( ReportDesignerParserModule.NAMESPACE,
        "visual-height", new Double( visualHeightParsed ) );
    }

    final AbstractReportDefinition report = (AbstractReportDefinition)
      getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( rowBandingDefinitionReadHandler != null ) {
      final Expression expression = (Expression) rowBandingDefinitionReadHandler.getObject();
      if ( expression != null ) {
        report.addExpression( expression );
      }
    }

    if ( rootLevelBand instanceof AbstractRootLevelBand ) {
      final AbstractRootLevelBand arb = (AbstractRootLevelBand) rootLevelBand;

      for ( int i = 0; i < subreports.size(); i++ ) {
        final SubreportElementReadHandler readHandler = (SubreportElementReadHandler) subreports.get( i );
        final SubReport subReport = (SubReport) readHandler.getObject();
        arb.addSubReport( subReport );
      }
    }

    if ( verticalLinealModel != null ) {
      final Guideline[] guidelines = verticalLinealModel.getGuidelineValues();
      if ( guidelines != null && guidelines.length > 0 ) {
        final StringBuffer b = new StringBuffer( 100 );
        for ( int i = 0; i < guidelines.length; i++ ) {
          final Guideline guideline = guidelines[ i ];
          if ( i != 0 ) {
            b.append( ' ' );
          }
          b.append( guideline.externalize() );
        }
        rootLevelBand.setAttribute( ReportDesignerParserModule.NAMESPACE,
          ReportDesignerParserModule.VERTICAL_GUIDE_LINES_ATTRIBUTE, b.toString() );
      }
    }

  }
}
