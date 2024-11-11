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
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 20.08.2007, 19:29:20
 *
 * @author Thomas Morgner
 */
public class ResultChunkReadHandler extends AbstractXmlReadHandler {
  private ResultTable resultTable;
  private int row;

  public ResultChunkReadHandler() {
    resultTable = new ResultTable();
    row = -1;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( ObjectUtilities.equal( uri, getUri() ) == false ) {
      return null;
    }
    if ( "row".equals( tagName ) ) {
      row += 1;
      return new RowReadHandler( resultTable, row );
    }
    return null;
  }

  public Object getObject() throws SAXException {
    return resultTable;
  }
}
