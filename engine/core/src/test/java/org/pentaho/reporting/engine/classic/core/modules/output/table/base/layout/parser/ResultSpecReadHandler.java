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
