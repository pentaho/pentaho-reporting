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

package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GroupDefinitionsReadHandler extends AbstractXmlReadHandler {
  private ArrayList readHandlers;
  private GroupDefinition[] result;

  public GroupDefinitionsReadHandler() {
    readHandlers = new ArrayList();
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "group-definition".equals( tagName ) ) {
      final GroupDefinitionReadHandler readHandler = new GroupDefinitionReadHandler();
      readHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    result = new GroupDefinition[ readHandlers.size() ];
    for ( int i = 0; i < readHandlers.size(); i++ ) {
      final GroupDefinitionReadHandler handler = (GroupDefinitionReadHandler) readHandlers.get( i );
      result[ i ] = (GroupDefinition) handler.getObject();
    }
  }

  public Object getObject() throws SAXException {
    return getGroupDefinitions();
  }

  public GroupDefinition[] getGroupDefinitions() {
    return (GroupDefinition[]) result.clone();
  }
}
