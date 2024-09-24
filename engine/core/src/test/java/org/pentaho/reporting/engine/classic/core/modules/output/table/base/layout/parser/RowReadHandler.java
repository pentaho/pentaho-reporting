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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 21.08.2007, 15:21:22
 *
 * @author Thomas Morgner
 */
public class RowReadHandler extends AbstractXmlReadHandler {
  private ResultTable table;
  private int row;
  private int column;

  public RowReadHandler( final ResultTable table, final int row ) {
    this.table = table;
    this.row = row;
    this.column = -1;
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( ObjectUtilities.equal( uri, getUri() ) == false ) {
      return null;
    }
    if ( "cell".equals( tagName ) ) {
      column += 1;
      return new CellReadHandler( table, row, column );
    }
    if ( "empty-cell".equals( tagName ) ) {
      column += 1;
      return new EmptyCellReadHandler( table, row, column );
    }
    if ( "covered-cell".equals( tagName ) ) {
      column += 1;
      return new IgnoreAnyChildReadHandler();
    }
    return null;
  }

  public Object getObject() throws SAXException {
    return null;
  }
}
