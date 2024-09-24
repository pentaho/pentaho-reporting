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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Fill me.
 *
 * @author Thomas Morgner
 */
public class SubReportParameterDefinitionReadHandler extends AbstractXmlReadHandler {
  private ArrayList importParameters;
  private ParameterMapping[] parameterMappings;

  public SubReportParameterDefinitionReadHandler() {
    importParameters = new ArrayList();
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
    if ( BundleNamespaces.DATADEFINITION.equals( uri ) == false ) {
      return null;
    }
    if ( "plain-parameter".equals( tagName ) ) {
      final PlainParameterReadHandler readHandler = new PlainParameterReadHandler();
      importParameters.add( readHandler );
      return readHandler;
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
    parameterMappings = new ParameterMapping[importParameters.size()];
    for ( int i = 0; i < importParameters.size(); i++ ) {
      final PlainParameterReadHandler handler = (PlainParameterReadHandler) importParameters.get( i );
      parameterMappings[i] = new ParameterMapping( handler.getName(), handler.getName() );
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
    return null;
  }

  public ParameterMapping[] getImportParameter() {
    return parameterMappings;
  }
}
