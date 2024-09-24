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

import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public abstract class AbstractImageElementReadHandler extends AbstractElementReadHandler {
  private static final String SCALE_ATT = "scale";
  private static final String KEEP_ASPECT_RATIO_ATT = "keepAspectRatio";

  protected AbstractImageElementReadHandler() {
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
    handleScale( atts );
    handleKeepAspectRatio( atts );
  }

  protected void handleScale( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractImageElementReadHandler.SCALE_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean scale = ParserUtil.parseBoolean( booleanValue, getLocator() );
    elementFactory.setScale( scale );
  }

  protected void handleKeepAspectRatio( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractImageElementReadHandler.KEEP_ASPECT_RATIO_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean keepAspectRatio = ParserUtil.parseBoolean( booleanValue, getLocator() );
    elementFactory.setKeepAspectRatio( keepAspectRatio );
  }
}
