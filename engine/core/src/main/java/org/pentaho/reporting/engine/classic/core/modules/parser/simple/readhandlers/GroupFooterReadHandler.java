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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.xml.sax.SAXException;

public class GroupFooterReadHandler extends RootLevelBandReadHandler {
  private static final Log logger = LogFactory.getLog( GroupFooterReadHandler.class );

  public GroupFooterReadHandler( final Band band ) {
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
      GroupFooterReadHandler.logger.warn( "The 'pagebreak' attribute of the <group-footer> tag is deprecated. "
          + "Use the 'pagebreak-after' attribute instead." );
      final Boolean ownPage = ParserUtil.parseBoolean( ownPageAttr, getLocator() );
      getBand().getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, ownPage );
    }
  }
}
