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
