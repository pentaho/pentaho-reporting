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

import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Arrays;

public class RelationalGroupReadHandler extends AbstractElementReadHandler {
  private GroupHeaderReadHandler headerReadHandler;
  private GroupFooterReadHandler footerReadHandler;
  private GroupFieldsReadHandler fieldsReadHandler;
  private DataGroupBodyReadHandler dataBodyReadHandler;
  private SubGroupBodyReadHandler subGroupBodyReadHandler;

  public RelationalGroupReadHandler() throws ParseException {
    super( RelationalGroupType.INSTANCE );
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
      if ( "group-header".equals( tagName ) ) {
        if ( headerReadHandler == null ) {
          headerReadHandler = new GroupHeaderReadHandler();
        }
        return headerReadHandler;
      }
      if ( "group-footer".equals( tagName ) ) {
        if ( footerReadHandler == null ) {
          footerReadHandler = new GroupFooterReadHandler();
        }
        return footerReadHandler;
      }
      if ( "fields".equals( tagName ) ) {
        if ( fieldsReadHandler == null ) {
          fieldsReadHandler = new GroupFieldsReadHandler();
        }
        return fieldsReadHandler;
      }
      if ( "group-body".equals( tagName ) ) {
        subGroupBodyReadHandler = new SubGroupBodyReadHandler();
        return subGroupBodyReadHandler;
      }
      if ( "data-body".equals( tagName ) ) {
        dataBodyReadHandler = new DataGroupBodyReadHandler();
        return dataBodyReadHandler;
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

    final RelationalGroup group = getElement();
    if ( fieldsReadHandler != null ) {
      final String[] fields = fieldsReadHandler.getFields();
      group.setFields( Arrays.asList( fields ) );
    }
    if ( headerReadHandler != null ) {
      group.setHeader( headerReadHandler.getElement() );
    }
    if ( footerReadHandler != null ) {
      group.setFooter( footerReadHandler.getElement() );
    }
    if ( subGroupBodyReadHandler != null ) {
      group.setBody( subGroupBodyReadHandler.getElement() );
    } else if ( dataBodyReadHandler != null ) {
      group.setBody( dataBodyReadHandler.getElement() );
    }
  }

  public RelationalGroup getElement() {
    return (RelationalGroup) super.getElement();
  }
}
