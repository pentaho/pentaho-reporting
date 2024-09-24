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
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.SourceChunk;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ValidationSequence;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Creation-Date: 20.08.2007, 19:27:52
 *
 * @author Thomas Morgner
 */
public class ResultSpecReadHandler extends AbstractXmlReadHandler {
  private ValidationSequence validationSequence;
  private ArrayList chunks;

  public ResultSpecReadHandler() {
    chunks = new ArrayList();
    validationSequence = new ValidationSequence();

  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    final float pageWidth = ParserUtil.parseFloat( attrs.getValue( getUri(), "page-width" ), "No page width given?" );
    if ( pageWidth <= 0 ) {
      throw new ParseException( "The page-width must be greater than zero" );
    }
    validationSequence.setPageWidth( (int) pageWidth );

    final String mode = attrs.getValue( getUri(), "mode" );
    validationSequence.setStrict( "strict".equals( mode ) );
  }

  /**
   * Returns the handler for a child element.
   *
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
    if ( "source".equals( tagName ) ) {
      final SourceChunkReadHandler readHandler = new SourceChunkReadHandler();
      chunks.add( readHandler );
      return readHandler;
    }
    if ( "result".equals( tagName ) ) {
      final ResultChunkReadHandler readHandler = new ResultChunkReadHandler();
      chunks.add( readHandler );
      return readHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < chunks.size(); i++ ) {
      final XmlReadHandler readHandler = (XmlReadHandler) chunks.get( i );
      final Object object = readHandler.getObject();
      if ( object instanceof SourceChunk ) {
        validationSequence.addSourceChunk( (SourceChunk) object );
      } else if ( object instanceof ResultTable ) {
        validationSequence.addResultTable( (ResultTable) object );
      }
    }
  }

  public Object getObject() throws SAXException {
    return validationSequence;
  }
}
