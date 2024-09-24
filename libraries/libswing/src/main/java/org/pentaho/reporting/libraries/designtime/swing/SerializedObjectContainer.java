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

package org.pentaho.reporting.libraries.designtime.swing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SerializedObjectContainer implements Serializable {
  public static class DebuggingObjectOutputStream
    extends ObjectOutputStream {

    private static final Field DEPTH_FIELD;

    static {
      try {
        DEPTH_FIELD = ObjectOutputStream.class.getDeclaredField( "depth" ); // NON-NLS
        DEPTH_FIELD.setAccessible( true );
      } catch ( NoSuchFieldException e ) {
        throw new AssertionError( e );
      }
    }

    private final List<Object> stack;

    /**
     * Indicates whether or not OOS has tried to write an IOException (presumably as the result of a serialization
     * error) to the stream.
     */
    private boolean broken;

    public DebuggingObjectOutputStream( final OutputStream out ) throws IOException {
      super( out );
      broken = false;
      stack = new ArrayList<Object>();
      enableReplaceObject( true );
    }

    /**
     * Abuse {@code replaceObject()} as a hook to maintain our stack.
     */
    protected Object replaceObject( final Object o ) {
      // ObjectOutputStream writes serialization
      // exceptions to the stream. Ignore
      // everything after that so we don't lose
      // the path to a non-serializable object. So
      // long as the user doesn't write an
      // IOException as the root object, we're OK.
      final int currentDepth = currentDepth();
      if ( o instanceof IOException
        && currentDepth == 0 ) {
        broken = true;
      }
      if ( !broken ) {
        truncate( currentDepth );
        stack.add( o );
      }
      return o;
    }

    private void truncate( final int depth ) {
      while ( stack.size() > depth ) {
        pop();
      }
    }

    private Object pop() {
      return stack.remove( stack.size() - 1 );
    }

    /**
     * Returns a 0-based depth within the object graph of the current object being serialized.
     *
     * @return the current depth.
     */
    private int currentDepth() {
      try {
        final Integer oneBased = ( (Integer) DEPTH_FIELD.get( this ) );
        return oneBased - 1;
      } catch ( IllegalAccessException e ) {
        throw new AssertionError( e );
      }
    }

    /**
     * Returns the path to the last object serialized. If an exception occurred, this should be the path to the
     * non-serializable object.
     *
     * @return Returns the current call stack.
     */
    public List<Object> getStack() {
      return stack;
    }
  }

  private static final Log logger = LogFactory.getLog( SerializedObjectContainer.class );
  private transient Object[] data;

  public SerializedObjectContainer( final Object[] data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.data = data.clone();

    // some paranoid checks ..
    try {
      final DebuggingObjectOutputStream objectOutputStream = new DebuggingObjectOutputStream( new NullOutputStream() );
      try {
        objectOutputStream.writeObject( this );
      } catch ( NotSerializableException nse ) {
        logger.debug( "Non-Serializable object found @Path: " + objectOutputStream.getStack() ); // NON-NLS
        throw nse;
      }
    } catch ( IOException e ) {
      throw new IllegalStateException( "Object is not serializable." );
    }
  }

  public Object[] getData() {
    return data.clone();
  }

  private void writeObject( final ObjectOutputStream stream )
    throws IOException {
    stream.defaultWriteObject();
    final int count = data.length;
    stream.writeObject( data.getClass().getComponentType() );
    stream.writeInt( count );
    for ( int i = 0; i < count; i++ ) {
      final Object object = data[ i ];
      if ( object != null && object instanceof Serializable ) {
        stream.writeInt( i );
        stream.writeObject( object );
      } else {
        stream.writeInt( -1 );
      }
    }
  }

  private void readObject( final ObjectInputStream stream )
    throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    final Class arrayType = (Class) stream.readObject();
    final int count = stream.readInt();
    data = (Object[]) Array.newInstance( arrayType, count );
    for ( int i = 0; i < count; i++ ) {
      final int index = stream.readInt();
      if ( index != -1 ) {
        data[ i ] = stream.readObject();
      }
    }
  }
}
