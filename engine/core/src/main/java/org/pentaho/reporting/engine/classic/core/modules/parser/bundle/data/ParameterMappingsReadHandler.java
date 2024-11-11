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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ParameterMappingReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class ParameterMappingsReadHandler extends AbstractXmlReadHandler {
  private ArrayList inputParameters;
  private ArrayList exportParameters;
  private ParameterMapping[] inputMapping;
  private ParameterMapping[] exportMapping;

  public ParameterMappingsReadHandler() {
    inputParameters = new ArrayList();
    exportParameters = new ArrayList();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "input-parameter".equals( tagName ) ) {
      final ParameterMappingReadHandler readHandler = new ParameterMappingReadHandler();
      inputParameters.add( readHandler );
      return readHandler;
    } else if ( "export-parameter".equals( tagName ) ) {
      final ParameterMappingReadHandler readHandler = new ParameterMappingReadHandler();
      exportParameters.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    inputMapping = new ParameterMapping[inputParameters.size()];
    for ( int i = 0; i < inputMapping.length; i++ ) {
      final ParameterMappingReadHandler o = (ParameterMappingReadHandler) inputParameters.get( i );
      inputMapping[i] = new ParameterMapping( o.getName(), o.getAlias() );
    }

    exportMapping = new ParameterMapping[exportParameters.size()];
    for ( int i = 0; i < exportMapping.length; i++ ) {
      final ParameterMappingReadHandler o = (ParameterMappingReadHandler) exportParameters.get( i );
      exportMapping[i] = new ParameterMapping( o.getName(), o.getAlias() );
    }
  }

  public ParameterMapping[] getInputMapping() {
    return (ParameterMapping[]) inputMapping.clone();
  }

  public ParameterMapping[] getExportMapping() {
    return (ParameterMapping[]) exportMapping.clone();
  }

  public Object getObject() throws SAXException {
    return null;
  }
}
