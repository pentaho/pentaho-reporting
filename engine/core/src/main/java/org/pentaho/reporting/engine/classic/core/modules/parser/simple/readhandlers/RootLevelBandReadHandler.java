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

import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.IncludeSubReportReadHandler;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class RootLevelBandReadHandler extends BandReadHandler {
  /**
   * Literal text for an XML attribute.
   */
  public static final String FIXED_POSITION_ATTRIBUTE = "fixed-position";

  /**
   * Literal text for an XML attribute.
   */
  public static final String PAGEBREAK_BEFORE_ATTR = "pagebreak-before-print";

  /**
   * Literal text for an XML attribute.
   */
  public static final String HEIGHT_ATTRIBUTE = "height";

  /**
   * Literal text for an XML attribute.
   */
  public static final String PAGEBREAK_AFTER_ATTRIBUTE = "pagebreak-after-print";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ALIGNMENT_ATT = "alignment";

  /**
   * Literal text for an XML attribute.
   */
  public static final String VALIGNMENT_ATT = "vertical-alignment";

  private ArrayList subReportHandlers;

  public RootLevelBandReadHandler( final Band band ) {
    super( band );
    subReportHandlers = new ArrayList();
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
    handleHeight( attr );
    handleFixedPosition( attr );

    if ( isManualBreakAllowed() ) {
      handleBreakAfter( attr );
      handleBreakBefore( attr );
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "sub-report".equals( tagName ) ) {
        final IncludeSubReportReadHandler subReportReadHandler = new IncludeSubReportReadHandler();
        subReportHandlers.add( subReportReadHandler );
        return subReportReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  protected boolean isManualBreakAllowed() {
    return true;
  }

  private void handleFixedPosition( final PropertyAttributes attr ) throws SAXException {
    final String fixedPos = attr.getValue( getUri(), RootLevelBandReadHandler.FIXED_POSITION_ATTRIBUTE );
    if ( fixedPos != null ) {
      final float fixedPosValue = ParserUtil.parseFloat( fixedPos, "FixedPosition is invalid!", getLocator() );
      getBand().getStyle().setStyleProperty( BandStyleKeys.FIXED_POSITION, new Float( fixedPosValue ) );
    }
  }

  private void handleHeight( final PropertyAttributes attr ) throws ParseException {
    final String height = attr.getValue( getUri(), RootLevelBandReadHandler.HEIGHT_ATTRIBUTE );
    if ( height != null ) {
      final float heightValue = ParserUtil.parseFloat( height, "Height is invalid.", getLocator() );
      getBand().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( heightValue ) );
    }
  }

  private void handleBreakBefore( final PropertyAttributes attr ) throws SAXException {
    final String breakBeforeAttr = attr.getValue( getUri(), RootLevelBandReadHandler.PAGEBREAK_BEFORE_ATTR );
    final Boolean breakBefore = ParserUtil.parseBoolean( breakBeforeAttr, getLocator() );
    getBand().getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, breakBefore );
  }

  private void handleBreakAfter( final PropertyAttributes attr ) throws SAXException {
    final String breakAfterAttr = attr.getValue( getUri(), RootLevelBandReadHandler.PAGEBREAK_AFTER_ATTRIBUTE );
    if ( breakAfterAttr != null ) {
      final Boolean breakAfter = ParserUtil.parseBoolean( breakAfterAttr, getLocator() );
      getBand().getStyle().setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, breakAfter );
    }
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Band band = getBand();
    if ( band instanceof AbstractRootLevelBand ) {
      final AbstractRootLevelBand arlb = (AbstractRootLevelBand) band;
      for ( int i = 0; i < subReportHandlers.size(); i++ ) {
        final IncludeSubReportReadHandler handler = (IncludeSubReportReadHandler) subReportHandlers.get( i );
        arlb.addSubReport( (SubReport) handler.getObject() );
      }
    }
  }
}
