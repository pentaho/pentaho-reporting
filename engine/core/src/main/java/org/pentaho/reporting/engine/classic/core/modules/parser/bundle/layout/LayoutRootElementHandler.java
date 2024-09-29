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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.AbstractElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.CrosstabGroupReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.RelationalGroupReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ReportFooterReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ReportHeaderReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class LayoutRootElementHandler extends AbstractElementReadHandler {
  private AbstractReportDefinition report;
  private LayoutProcessorReadHandler layoutProcessorHandler;
  private ArrayList<LayoutPreprocessorReadHandler> layoutPreprocessorHandlers;
  private ReportHeaderReadHandler reportHeaderReadHandler;
  private RelationalGroupReadHandler rootGroupReadHandler;
  private ReportFooterReadHandler reportFooterReadHandler;
  private CrosstabGroupReadHandler crosstabReadHandler;

  public LayoutRootElementHandler() {
    layoutPreprocessorHandlers = new ArrayList<LayoutPreprocessorReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final RootXmlReadHandler rootHandler = getRootHandler();
    final Object maybeReport = rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( maybeReport instanceof SubReport ) {
      report = (SubReport) maybeReport;
    } else if ( maybeReport instanceof MasterReport ) {
      report = (MasterReport) maybeReport;
    } else {
      throw new IllegalStateException( "Layout.xml cannot be parsed on its own. It needs to have a report-instance." );
    }

    initialize( report.getElementType() );
    super.startParsing( attrs );
  }

  protected Element createElement() throws ParseException {
    // not a real create, more a cheating ..
    return report;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "layout-processors".equals( tagName ) ) {
        layoutProcessorHandler = new LayoutProcessorReadHandler();
        return layoutProcessorHandler;
      }

      if ( "preprocessor".equals( tagName ) ) {
        final LayoutPreprocessorReadHandler layoutPreprocessorHandler = new LayoutPreprocessorReadHandler();
        layoutPreprocessorHandlers.add( layoutPreprocessorHandler );
        return layoutPreprocessorHandler;
      }

      if ( "report-header".equals( tagName ) ) {
        if ( reportHeaderReadHandler == null ) {
          reportHeaderReadHandler = new ReportHeaderReadHandler();
        }
        return reportHeaderReadHandler;
      }

      if ( "crosstab".equals( tagName ) ) {
        if ( crosstabReadHandler == null ) {
          crosstabReadHandler = new CrosstabGroupReadHandler();
        }
        return crosstabReadHandler;
      }
      if ( "group".equals( tagName ) ) {
        if ( rootGroupReadHandler == null ) {
          rootGroupReadHandler = new RelationalGroupReadHandler();
        }
        return rootGroupReadHandler;
      }

      if ( "report-footer".equals( tagName ) ) {
        if ( reportFooterReadHandler == null ) {
          reportFooterReadHandler = new ReportFooterReadHandler();
        }
        return reportFooterReadHandler;
      }
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( layoutProcessorHandler != null ) {
      final Expression[] expressions = layoutProcessorHandler.getExpressions();
      for ( int i = 0; i < expressions.length; i++ ) {
        final Expression expression = expressions[i];
        report.addExpression( expression );
      }
    }

    for ( int i = 0; i < layoutPreprocessorHandlers.size(); i++ ) {
      final LayoutPreprocessorReadHandler handler = layoutPreprocessorHandlers.get( i );
      report.addPreProcessor( handler.getPreProcessor() );
    }
    if ( reportHeaderReadHandler != null ) {
      report.setReportHeader( (ReportHeader) reportHeaderReadHandler.getElement() );
    }
    if ( rootGroupReadHandler != null ) {
      report.setRootGroup( rootGroupReadHandler.getElement() );
    } else if ( crosstabReadHandler != null ) {
      report.setRootGroup( crosstabReadHandler.getElement() );
    }
    if ( reportFooterReadHandler != null ) {
      report.setReportFooter( (ReportFooter) reportFooterReadHandler.getElement() );
    }
  }
}
