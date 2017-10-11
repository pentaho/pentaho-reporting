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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Arrays;

public class CrosstabRowGroupReadHandler extends AbstractElementReadHandler {
  private CrosstabHeaderBandReadHandler headerReadHandler;
  private CrosstabSummaryHeaderBandReadHandler summaryHeaderBandReadHandler;
  private CrosstabTitleHeaderBandReadHandler titleHeaderBandReadHandler;
  private CrosstabRowSubGroupBodyReadHandler rowSubGroupBodyReadHandler;
  private StringReadHandler fieldReadHandler;
  private CrosstabColumnSubGroupBodyReadHandler columnSubGroupBodyReadHandler;
  private GroupHeaderReadHandler legacyHeaderReadHandler;

  public CrosstabRowGroupReadHandler() throws ParseException {
    super( CrosstabRowGroupType.INSTANCE );
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleNamespaces.LAYOUT.equals( uri ) ) {
      if ( "field".equals( tagName ) ) {
        if ( fieldReadHandler == null ) {
          fieldReadHandler = new StringReadHandler();
        }
        return fieldReadHandler;
      }
      if ( "crosstab-header".equals( tagName ) ) {
        if ( headerReadHandler == null ) {
          headerReadHandler = new CrosstabHeaderBandReadHandler();
        }
        return headerReadHandler;
      }
      if ( "group-header".equals( tagName ) ) {
        if ( legacyHeaderReadHandler == null ) {
          legacyHeaderReadHandler = new GroupHeaderReadHandler();
        }
        return legacyHeaderReadHandler;
      }
      if ( "crosstab-title-header".equals( tagName ) ) {
        if ( titleHeaderBandReadHandler == null ) {
          titleHeaderBandReadHandler = new CrosstabTitleHeaderBandReadHandler();
        }
        return titleHeaderBandReadHandler;
      }
      if ( "crosstab-summary-header".equals( tagName ) ) {
        if ( summaryHeaderBandReadHandler == null ) {
          summaryHeaderBandReadHandler = new CrosstabSummaryHeaderBandReadHandler();
        }
        return summaryHeaderBandReadHandler;
      }
      if ( "crosstab-column-group-body".equals( tagName ) ) {
        columnSubGroupBodyReadHandler = new CrosstabColumnSubGroupBodyReadHandler();
        return columnSubGroupBodyReadHandler;
      }
      if ( "crosstab-row-group-body".equals( tagName ) ) {
        rowSubGroupBodyReadHandler = new CrosstabRowSubGroupBodyReadHandler();
        return rowSubGroupBodyReadHandler;
      }
      if ( "crosstab-title-footer".equals( tagName ) || "crosstab-summary-footer".equals( tagName )
          || "group-footer".equals( tagName ) ) {
        return new IgnoreAnyChildReadHandler();
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();

    final CrosstabRowGroup group = getElement();
    if ( fieldReadHandler != null ) {
      group.setField( fieldReadHandler.getResult() );
    }
    if ( headerReadHandler != null ) {
      group.setHeader( headerReadHandler.getElement() );
    } else if ( legacyHeaderReadHandler != null ) {
      final CrosstabHeader header = new CrosstabHeader();
      legacyHeaderReadHandler.getElement().copyInto( header );
      header.addElements( Arrays.asList( legacyHeaderReadHandler.getElement().getElementArray() ) );
      group.setHeader( header );
    }

    if ( summaryHeaderBandReadHandler != null ) {
      group.setSummaryHeader( summaryHeaderBandReadHandler.getElement() );
    }
    if ( titleHeaderBandReadHandler != null ) {
      group.setTitleHeader( titleHeaderBandReadHandler.getElement() );
    }

    if ( rowSubGroupBodyReadHandler != null ) {
      group.setBody( rowSubGroupBodyReadHandler.getElement() );
    } else if ( columnSubGroupBodyReadHandler != null ) {
      group.setBody( columnSubGroupBodyReadHandler.getElement() );
    }
  }

  public CrosstabRowGroup getElement() {
    return (CrosstabRowGroup) super.getElement();
  }
}
