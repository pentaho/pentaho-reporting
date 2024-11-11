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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ContentElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.SAXException;

public class DrawableRefReadHandler extends AbstractElementReadHandler {
  private ContentElementFactory elementFactory;

  public DrawableRefReadHandler() {
    this.elementFactory = new ContentElementFactory();
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );

    final String content = atts.getValue( getUri(), "src" );
    if ( content == null ) {
      throw new SAXException( "Required attribute 'src' is missing." );
    }

    elementFactory.setContent( content );
    final ResourceManager resourceManager = getRootHandler().getResourceManager();
    elementFactory.setBaseURL( resourceManager.toURL( getRootHandler().getContext() ) );
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }
}
