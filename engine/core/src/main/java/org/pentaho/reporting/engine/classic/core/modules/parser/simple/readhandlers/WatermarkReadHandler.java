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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.xml.sax.SAXException;

public class WatermarkReadHandler extends RootLevelBandReadHandler {
  public WatermarkReadHandler( final Band band ) {
    super( band );
  }

  /**
   * Starts parsing.
   *
   * @param attr
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attr ) throws SAXException {
    super.startParsing( attr );
    handleSticky( attr );
  }

  private void handleSticky( final PropertyAttributes attr ) throws SAXException {
    final String repeat = attr.getValue( getUri(), "sticky" );
    final Boolean repeatVal = ParserUtil.parseBoolean( repeat, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.STICKY, repeatVal );
  }

  protected boolean isManualBreakAllowed() {
    return false;
  }

}
