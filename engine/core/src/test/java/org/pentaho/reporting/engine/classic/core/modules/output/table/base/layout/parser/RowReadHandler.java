/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
