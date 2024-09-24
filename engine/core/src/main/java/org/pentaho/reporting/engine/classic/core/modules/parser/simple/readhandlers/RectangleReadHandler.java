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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.xml.sax.SAXException;

public class RectangleReadHandler extends AbstractShapeElementReadHandler {
  private RectangleElementFactory elementFactory;

  public RectangleReadHandler() {
    elementFactory = new RectangleElementFactory();
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
    if ( elementFactory.getShouldDraw() == null ) {
      elementFactory.setShouldDraw( Boolean.TRUE );
    }
    if ( elementFactory.getShouldFill() == null ) {
      elementFactory.setShouldFill( Boolean.TRUE );
    }
    elementFactory.setScale( Boolean.TRUE );
    elementFactory.setDynamicHeight( Boolean.FALSE );
    elementFactory.setKeepAspectRatio( Boolean.FALSE );
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }
}
