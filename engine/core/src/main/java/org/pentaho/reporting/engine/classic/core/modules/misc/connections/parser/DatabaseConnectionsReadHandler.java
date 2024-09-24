package org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

public class DatabaseConnectionsReadHandler extends AbstractXmlReadHandler {
  private static final String NODE_ROOT = "databaseMeta"; //$NON-NLS-1$

  private DatabaseConnectionCollection collection;
  private ArrayList<DatabaseMetaReadHandler> readHandlers;

  public DatabaseConnectionsReadHandler() {
    readHandlers = new ArrayList<DatabaseMetaReadHandler>();
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( NODE_ROOT.equals( tagName ) ) {
      final DatabaseMetaReadHandler readHandler = new DatabaseMetaReadHandler();
      readHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    final ArrayList<IDatabaseConnection> connections = new ArrayList<IDatabaseConnection>();

    for ( int i = 0; i < readHandlers.size(); i++ ) {
      final DatabaseMetaReadHandler readHandler = readHandlers.get( i );
      connections.add( readHandler.getObject() );
    }
    try {
      collection =
          new DatabaseConnectionCollection( connections.toArray( new IDatabaseConnection[connections.size()] ) );
    } catch ( IOException e ) {
      throw new ParseException( e, getLocator() );
    }
  }

  public DatabaseConnectionCollection getObject() throws SAXException {
    return collection;
  }
}
