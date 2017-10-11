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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.xml.sax.SAXException;

public class PageBandReadHandler extends RootLevelBandReadHandler {
  /**
   * Literal text for an XML attribute.
   */
  public static final String ON_FIRST_PAGE_ATTR = "onfirstpage";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ON_LAST_PAGE_ATTR = "onlastpage";

  public PageBandReadHandler( final Band band ) {
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
    handleOnFirstPage( attr );
    handleOnLastPage( attr );
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

  private void handleOnFirstPage( final PropertyAttributes attr ) throws SAXException {
    final String breakBeforeAttr = attr.getValue( getUri(), PageBandReadHandler.ON_FIRST_PAGE_ATTR );
    final Boolean breakBefore = ParserUtil.parseBoolean( breakBeforeAttr, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, breakBefore );
  }

  private void handleOnLastPage( final PropertyAttributes attr ) throws SAXException {
    final String breakBeforeAttr = attr.getValue( getUri(), PageBandReadHandler.ON_LAST_PAGE_ATTR );
    final Boolean breakBefore = ParserUtil.parseBoolean( breakBeforeAttr, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, breakBefore );
  }
}
