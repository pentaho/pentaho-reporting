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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.SourceChunk;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 20.08.2007, 19:29:20
 *
 * @author Thomas Morgner
 */
public class SourceChunkReadHandler extends BandReadHandler {
  public SourceChunkReadHandler() {
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes attrs )
    throws SAXException {
    if ( ObjectUtilities.equal( uri, getUri() ) == false ) {
      return null;
    }
    if ( "band".equals( tagName ) ) {
      return super.getHandlerForChild( uri, tagName, attrs );
    }

    return null;
  }

  public Object getObject() {
    final Band band = (Band) super.getObject();
    return new SourceChunk( band );
  }
}
