/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 22.08.2007, 14:06:49
 *
 * @author Thomas Morgner
 */
public class EmptyCellReadHandler extends AbstractXmlReadHandler {
  private ResultTable table;
  private int row;
  private int column;

  public EmptyCellReadHandler( final ResultTable table, final int row, final int column ) {
    this.table = table;
    this.row = row;
    this.column = column;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    table.setResultCell( row, column, null );
  }

  public Object getObject() throws SAXException {
    return null;
  }
}
