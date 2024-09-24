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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabHeader;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Arrays;

public class CrosstabColumnGroupReadHandler extends AbstractElementReadHandler {
  private CrosstabHeaderBandReadHandler headerReadHandler;
  private CrosstabSummaryHeaderBandReadHandler summaryHeaderBandReadHandler;
  private CrosstabTitleHeaderBandReadHandler titleHeaderBandReadHandler;
  private CrosstabColumnSubGroupBodyReadHandler columnSubGroupBodyReadHandler;
  private DataGroupBodyReadHandler groupBodyReadHandler;
  private CrosstabCellBodyReadHandler cellBodyReadHandler;
  private GroupHeaderReadHandler legacyHeaderReadHandler;

  private StringReadHandler fieldReadHandler;

  public CrosstabColumnGroupReadHandler() throws ParseException {
    super( CrosstabColumnGroupType.INSTANCE );
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
      if ( "field".equals( tagName ) ) {
        if ( fieldReadHandler == null ) {
          fieldReadHandler = new StringReadHandler();
        }
        return fieldReadHandler;
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
      if ( "data-body".equals( tagName ) ) {
        groupBodyReadHandler = new DataGroupBodyReadHandler();
        return groupBodyReadHandler;
      }
      if ( "crosstab-cell-body".equals( tagName ) ) {
        cellBodyReadHandler = new CrosstabCellBodyReadHandler();
        return cellBodyReadHandler;
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

    final CrosstabColumnGroup group = getElement();
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
    if ( columnSubGroupBodyReadHandler != null ) {
      group.setBody( columnSubGroupBodyReadHandler.getElement() );
    } else if ( cellBodyReadHandler != null ) {
      group.setBody( cellBodyReadHandler.getElement() );
    } else {
      final GroupDataBody element = groupBodyReadHandler.getElement();
      final ItemBand itemBand = element.getItemBand();
      final CrosstabCell cell = new CrosstabCell();
      itemBand.copyInto( cell );
      cell.addElements( Arrays.asList( itemBand.getElementArray() ) );

      final CrosstabCellBody body = new CrosstabCellBody();
      body.addElement( cell );
      group.setBody( body );
    }
  }

  public CrosstabColumnGroup getElement() {
    return (CrosstabColumnGroup) super.getElement();
  }
}
