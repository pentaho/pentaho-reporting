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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.xml.sax.SAXException;

public class GroupHeaderReadHandler extends RootLevelBandReadHandler {
  private static final Log logger = LogFactory.getLog( GroupHeaderReadHandler.class );

  public GroupHeaderReadHandler( final Band band ) {
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
    handlePagebreakAttr( attr );
    handleRepeat( attr );
    handleSticky( attr );
  }

  private void handleSticky( final PropertyAttributes attr ) throws SAXException {
    final String repeat = attr.getValue( getUri(), "sticky" );
    final Boolean repeatVal = ParserUtil.parseBoolean( repeat, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.STICKY, repeatVal );
  }

  private void handleRepeat( final PropertyAttributes attr ) throws SAXException {
    final String repeat = attr.getValue( getUri(), "repeat" );
    final Boolean repeatVal = ParserUtil.parseBoolean( repeat, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.REPEAT_HEADER, repeatVal );
  }

  private void handlePagebreakAttr( final PropertyAttributes attr ) throws SAXException {
    final String ownPageAttr = attr.getValue( getUri(), "pagebreak" );
    if ( ownPageAttr != null ) {
      GroupHeaderReadHandler.logger.warn( "The 'pagebreak' attribute of the <group-header> tag is deprecated. "
          + "Use the 'pagebreak-before' attribute instead." );
      final Boolean ownPage = ParserUtil.parseBoolean( ownPageAttr, getLocator() );
      getBand().getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, ownPage );
    }
  }
}
