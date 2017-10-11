/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.SubReportDataDefinition;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;

/**
 * The content root handler is the first handler that is parsed when dealing with bundle-reports. This file contains all
 * the forwards to the various report-files.
 * <p/>
 * A bundle always contains a master-report. It is not possible to parse a full bundle into a subreport. However, it is
 * possible to parse the subreport xml files contained in a bundle into subreports, if needed.
 *
 * @author Thomas Morgner
 */
public class SubContentRootElementHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( SubContentRootElementHandler.class );
  private SubReport report;

  public SubContentRootElementHandler() {
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler
   *          the root handler.
   * @param tagName
   *          the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    rootHandler.setHelperObject( "property-expansion", Boolean.FALSE );
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
    final Object maybeReport = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( maybeReport instanceof SubReport == false ) {
      // replace it ..
      report = new SubReport();
      getRootHandler().setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );
    } else {
      report = (SubReport) maybeReport;
    }
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
    if ( BundleNamespaces.CONTENT.equals( uri ) == false ) {
      return null;
    }

    if ( "data-definition".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseDataDefinition( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseDataDefinition( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    if ( "styles".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseStyles( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseStyles( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    if ( "layout".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseLayout( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseLayout( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    // Now, after all the user-defined and global files have been parsed, finally override whatever had been
    // defined in these files with the contents from the bundle. This will merge all the settings from the bundle
    // with the global definitions but grants the local settings higer preference
    parseLocalFiles();
  }

  private void parseLocalFiles() throws ParseException {
    parseDataDefinition( "datadefinition.xml" );
    parseStyles( "styles.xml" );
    parseLayout( "layout.xml" );
  }

  private boolean parseLayout( final String layout ) throws ParseException {
    try {
      final SubReport report = (SubReport) performExternalParsing( layout, SubReport.class );
      return report == this.report;
    } catch ( ResourceLoadingException e ) {
      SubContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: " + layout );
      return false;
    }
  }

  private boolean parseStyles( final String stylefile ) throws ParseException {
    try {
      final SubReport report = (SubReport) performExternalParsing( stylefile, SubReport.class );
      return report == this.report;
    } catch ( ResourceLoadingException e ) {
      SubContentRootElementHandler.logger
          .warn( "Unable to parse the parameter for this bundle from file: " + stylefile );
      return false;
    }
  }

  private boolean parseDataDefinition( final String parameterFile ) throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), null );
      final SubReportDataDefinition dataDefinition =
          (SubReportDataDefinition) performExternalParsing( parameterFile, SubReportDataDefinition.class, parameters );
      report.setDataFactory( dataDefinition.getDataFactory() );
      report.setQuery( dataDefinition.getQuery() );
      report.setQueryLimit( dataDefinition.getQueryLimit() );
      report.setQueryTimeout( dataDefinition.getQueryTimeout() );
      final ParameterMapping[] inputMapping = dataDefinition.getImportParameters();
      for ( int i = 0; i < inputMapping.length; i++ ) {
        final ParameterMapping mapping = inputMapping[i];
        report.addInputParameter( mapping.getName(), mapping.getAlias() );
      }
      final ParameterMapping[] exportMapping = dataDefinition.getExportParameters();
      for ( int i = 0; i < exportMapping.length; i++ ) {
        final ParameterMapping mapping = exportMapping[i];
        report.addExportParameter( mapping.getName(), mapping.getAlias() );
      }

      final Expression[] expressions = dataDefinition.getExpressions();
      if ( expressions != null ) {
        for ( int i = 0; i < expressions.length; i++ ) {
          final Expression expression = expressions[i];
          report.addExpression( expression );
        }
      }

      return true;
    } catch ( ResourceLoadingException e ) {
      SubContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: "
          + parameterFile );
      return false;
    } catch ( ReportDataFactoryException e ) {
      throw new ParseException( "Unable to configure datafactory.", getLocator() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return report;
  }
}
