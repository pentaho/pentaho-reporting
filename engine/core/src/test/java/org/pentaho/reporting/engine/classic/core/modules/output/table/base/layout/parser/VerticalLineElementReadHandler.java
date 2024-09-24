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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.VerticalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.AbstractElementReadHandler;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 20.08.2007, 20:31:53
 *
 * @author Thomas Morgner
 */
public class VerticalLineElementReadHandler extends AbstractElementReadHandler {
  private VerticalLineElementFactory factory;

  public VerticalLineElementReadHandler() {
    factory = new VerticalLineElementFactory();
  }

  protected ElementFactory getElementFactory() {
    return factory;
  }

  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );

    factory.setName( atts.getValue( getUri(), "name" ) );
    factory.setScale( Boolean.TRUE );
    factory.setKeepAspectRatio( Boolean.FALSE );
    factory.setShouldFill( Boolean.FALSE );
    factory.setShouldDraw( Boolean.TRUE );
    factory.setStroke( ReportParserUtil.parseStroke( atts.getValue( getUri(), "stroke" ), 1 ) );
  }
}
