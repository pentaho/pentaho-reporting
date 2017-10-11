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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Fill me.
 *
 * @author Thomas Morgner
 */
public class MasterParameterDefinitionReadHandler extends AbstractXmlReadHandler {
  private DefaultParameterDefinition parameterDefinition;
  private ArrayList parameterReadHandlers;

  public MasterParameterDefinitionReadHandler() {
    parameterReadHandlers = new ArrayList();
    parameterDefinition = new DefaultParameterDefinition();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String validatorClass = attrs.getValue( getUri(), "validator" );
    if ( validatorClass == null ) {
      parameterDefinition.setValidator( new DefaultReportParameterValidator() );
    } else {
      final Object o =
          ObjectUtilities.loadAndInstantiate( CompatibilityMapperUtil.mapClassName( validatorClass ), getClass(),
              ReportParameterValidator.class );
      if ( o == null ) {
        throw new ParseException( "Valud given for 'validator' is invalid", getLocator() );
      }
      parameterDefinition.setValidator( (ReportParameterValidator) o );
    }
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "plain-parameter".equals( tagName ) ) {
      final PlainParameterReadHandler readHandler = new PlainParameterReadHandler();
      parameterReadHandlers.add( readHandler );
      return readHandler;
    }

    if ( "single-selection-parameter".equals( tagName ) ) {
      // This is actually deprecated .
      final ListParameterReadHandler readHandler = new ListParameterReadHandler();
      parameterReadHandlers.add( readHandler );
      return readHandler;
    }

    if ( "list-parameter".equals( tagName ) ) {
      final ListParameterReadHandler readHandler = new ListParameterReadHandler();
      parameterReadHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < parameterReadHandlers.size(); i++ ) {
      final XmlReadHandler handler = (XmlReadHandler) parameterReadHandlers.get( i );
      parameterDefinition.addParameterDefinition( (ParameterDefinitionEntry) handler.getObject() );
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
    return parameterDefinition;
  }
}
