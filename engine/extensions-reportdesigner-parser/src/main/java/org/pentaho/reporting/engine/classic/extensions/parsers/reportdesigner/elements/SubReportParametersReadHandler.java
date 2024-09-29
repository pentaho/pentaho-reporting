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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Properties;

public class SubReportParametersReadHandler extends PropertiesReadHandler {
  private ArrayList importParameters;
  private ParameterMapping[] importParameterMappings;
  private ArrayList exportParameters;
  private ParameterMapping[] exportParameterMappings;
  private boolean globalImport;
  private boolean globalExport;

  public SubReportParametersReadHandler() {
    importParameters = new ArrayList();
    exportParameters = new ArrayList();
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
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "padding".equals( tagName ) ) {
        return new IgnoreAnyChildReadHandler();
      }
      if ( "importParameter".equals( tagName ) ) {
        final SubReportParameterReadHandler readHandler = new SubReportParameterReadHandler();
        importParameters.add( readHandler );
        return readHandler;
      }
      if ( "exportParameter".equals( tagName ) ) {
        final SubReportParameterReadHandler readHandler = new SubReportParameterReadHandler();
        exportParameters.add( readHandler );
        return readHandler;
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
    globalExport = "true".equals( result.getProperty( "globalExport" ) );
    globalImport = "true".equals( result.getProperty( "globalImport" ) );

    importParameterMappings = new ParameterMapping[ importParameters.size() ];
    for ( int i = 0; i < importParameters.size(); i++ ) {
      final SubReportParameterReadHandler readHandler = (SubReportParameterReadHandler) importParameters.get( i );
      ParameterMapping parameter = new ParameterMapping( readHandler.getKey(), readHandler.getValue() );
      importParameterMappings[ i ] = parameter;
    }

    exportParameterMappings = new ParameterMapping[ exportParameters.size() ];
    for ( int i = 0; i < exportParameters.size(); i++ ) {
      final SubReportParameterReadHandler readHandler = (SubReportParameterReadHandler) exportParameters.get( i );
      ParameterMapping parameter = new ParameterMapping( readHandler.getKey(), readHandler.getValue() );
      exportParameterMappings[ i ] = parameter;
    }
  }

  public ParameterMapping[] getImportParameterMappings() {
    return importParameterMappings;
  }

  public ParameterMapping[] getExportParameterMappings() {
    return exportParameterMappings;
  }

  public boolean isGlobalImport() {
    return globalImport;
  }

  public boolean isGlobalExport() {
    return globalExport;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
