package org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser;

import org.pentaho.database.model.IDatabaseConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DatabaseConnectionCollection implements Serializable {
  private byte[] connections;

  public DatabaseConnectionCollection( final IDatabaseConnection[] result ) throws IOException {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bout );
    out.writeObject( result );
    out.close();

    connections = bout.toByteArray();
  }

  public IDatabaseConnection[] getConnections() throws IOException {
    try {
      // return a copy, by deserializing the result.
      final ByteArrayInputStream bin = new ByteArrayInputStream( connections );
      final ObjectInputStream in = new ObjectInputStream( bin );
      return (IDatabaseConnection[]) in.readObject();
    } catch ( ClassNotFoundException e ) {
      throw new IOException( "Unable to deserialize database connections.", e );
    }
  }
}
