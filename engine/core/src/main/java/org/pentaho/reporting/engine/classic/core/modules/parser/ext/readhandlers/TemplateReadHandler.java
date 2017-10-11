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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateDescription;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class TemplateReadHandler extends CompoundObjectReadHandler {
  private TemplateCollection templateCollection;
  private boolean nameRequired;

  public TemplateReadHandler( final boolean nameRequired ) {
    super( null );
    this.nameRequired = nameRequired;
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
    templateCollection =
        (TemplateCollection) rootHandler.getHelperObject( ReportDefinitionReadHandler.TEMPLATE_FACTORY_KEY );
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String templateName = attrs.getValue( getUri(), "name" );
    if ( nameRequired && templateName == null ) {
      throw new ParseException( "The 'name' attribute is required for template definitions", getRootHandler()
          .getDocumentLocator() );
    }
    final String references = attrs.getValue( getUri(), "references" );
    if ( references == null ) {
      throw new ParseException( "The 'references' attribute is required for template definitions", getRootHandler()
          .getDocumentLocator() );
    }
    TemplateDescription template = templateCollection.getTemplate( references );
    if ( template == null ) {
      throw new ParseException( "The template '" + references + "' is not defined", getRootHandler()
          .getDocumentLocator() );
    }

    // Clone the defined template ... we don't change the original ..
    template = (TemplateDescription) template.getInstance();
    if ( templateName != null ) {
      template.setName( templateName );
      templateCollection.addTemplate( template );
    }
    setObjectDescription( template );
  }
}
