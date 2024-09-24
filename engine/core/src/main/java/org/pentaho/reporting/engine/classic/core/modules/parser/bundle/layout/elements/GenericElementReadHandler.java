package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class GenericElementReadHandler extends AbstractElementReadHandler {
  public GenericElementReadHandler() {

  }

  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    autoInit();
  }
}
