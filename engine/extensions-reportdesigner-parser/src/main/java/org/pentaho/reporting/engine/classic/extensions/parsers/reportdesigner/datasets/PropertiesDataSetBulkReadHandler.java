/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class PropertiesDataSetBulkReadHandler extends AbstractXmlReadHandler {
  private ArrayList properties;

  public PropertiesDataSetBulkReadHandler() {
    properties = new ArrayList();
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "property".equals( tagName ) ) {
      final TypedPropertyReadHandler readHandler = new TypedPropertyReadHandler();
      properties.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final Object o = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( o instanceof MasterReport == false ) {
      return;
    }

    final MasterReport report = (MasterReport) o;
    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    for ( int i = 0; i < properties.size(); i++ ) {
      final TypedPropertyReadHandler readHandler = (TypedPropertyReadHandler) properties.get( i );
      final String parameterName = readHandler.getName();
      if ( "report.date".equals( parameterName ) ) {
        continue;
      }
      if ( "report.name".equals( parameterName ) ) {
        continue;
      }

      final PlainParameter parameter = new PlainParameter( parameterName );
      parameter.setValueType( readHandler.getType() );
      parameter.setParameterAttribute
        ( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE, "textbox" );
      parameter.setDefaultValue( readHandler.getObject() );
      parameterDefinition.addParameterDefinition( parameter );
    }

    report.setParameterDefinition( parameterDefinition );
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
