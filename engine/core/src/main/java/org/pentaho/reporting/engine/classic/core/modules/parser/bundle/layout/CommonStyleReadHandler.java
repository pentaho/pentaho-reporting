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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CommonStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;

  public CommonStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String anchorName = attrs.getValue( getUri(), "anchor-name" );
    if ( anchorName != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.ANCHOR_NAME, anchorName );
    }

    final String hrefTarget = attrs.getValue( getUri(), "href-target" );
    if ( hrefTarget != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TARGET, hrefTarget );
    }
    final String hrefTitle = attrs.getValue( getUri(), "href-title" );
    if ( hrefTitle != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, hrefTitle );
    }
    final String hrefWindow = attrs.getValue( getUri(), "href-window" );
    if ( hrefWindow != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, hrefWindow );
    }

    final String boxSizing = attrs.getValue( getUri(), "box-sizing" );
    if ( boxSizing != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.BOX_SIZING, parseBoxSizing( boxSizing ) );
    }

    final String visible = attrs.getValue( getUri(), "visible" );
    if ( visible != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.VISIBLE, "true".equals( visible ) );
    }

    final String invisibleConsumesSpace = attrs.getValue( getUri(), "invisible-consumes-space" );
    if ( invisibleConsumesSpace != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, "true"
          .equals( invisibleConsumesSpace ) );
    }

    final String widows = attrs.getValue( getUri(), "widows" );
    if ( widows != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.WIDOWS, ReportParserUtil.parseInteger( widows, getLocator() ) );
    }

    final String widowsOrphanOptOut = attrs.getValue( getUri(), ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT.getName() );
    if ( widowsOrphanOptOut != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, ReportParserUtil.parseBoolean(
          widowsOrphanOptOut, getLocator() ) );
    }

    final String orphans = attrs.getValue( getUri(), ElementStyleKeys.ORPHANS.getName() );
    if ( orphans != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.ORPHANS, ReportParserUtil.parseInteger( orphans, getLocator() ) );
    }

    final String overflowX = attrs.getValue( getUri(), "overflow-x" );
    if ( overflowX != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.OVERFLOW_X, "true".equals( overflowX ) );
    }

    final String overflowY = attrs.getValue( getUri(), "overflow-y" );
    if ( overflowY != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.OVERFLOW_Y, "true".equals( overflowY ) );
    }

    final String avoidPagebreak = attrs.getValue( getUri(), "avoid-page-break" );
    if ( avoidPagebreak != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, "true".equals( avoidPagebreak ) );
    }

    final String valignment = attrs.getValue( getUri(), "vertical-alignment" );
    if ( valignment != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.VALIGNMENT, ReportParserUtil.parseVerticalElementAlignment(
          valignment, getLocator() ) );
    }

    final String alignment = attrs.getValue( getUri(), "alignment" );
    if ( alignment != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, ReportParserUtil.parseHorizontalElementAlignment(
          alignment, getLocator() ) );
    }
  }

  private Object parseBoxSizing( final String s ) {
    if ( BoxSizing.CONTENT_BOX.toString().equals( s ) ) {
      return BoxSizing.CONTENT_BOX;
    }
    return BoxSizing.BORDER_BOX;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
