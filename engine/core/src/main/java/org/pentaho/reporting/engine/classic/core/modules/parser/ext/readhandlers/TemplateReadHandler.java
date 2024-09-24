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
