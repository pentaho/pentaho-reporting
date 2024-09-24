/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.serializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;


/**
 * The SerializeHelper is used to make implementing custom serialization handlers easier. Handlers for certain object
 * types need to be added to this helper before this implementation is usable.
 *
 * @author Thomas Morgner
 */
public class SerializerHelper {
  private static final Log logger = LogFactory.getLog( SerializerHelper.class );
  /**
   * The singleton instance of the serialize helper.
   */
  private static SerializerHelper singleton;

  /**
   * Returns or creates a new SerializerHelper. When a new instance is created by this method, all known
   * SerializeMethods are registered.
   *
   * @return the SerializerHelper singleton instance.
   */
  public static synchronized SerializerHelper getInstance() {
    if ( singleton == null ) {
      singleton = LibSerializerBoot.getInstance().getObjectFactory().get( SerializerHelper.class );
      singleton.registerMethods();
    }
    return singleton;
  }

  /**
   * A collection of the serializer methods.
   */
  private final HashMap<Class, SerializeMethod> methods;

  /**
   * A class comparator for searching the super class of an certain class.
   */
  private final ClassComparator comparator;

  /**
   * Creates a new SerializerHelper.
   */
  public SerializerHelper() {
    this.comparator = new ClassComparator();
    this.methods = new HashMap<Class, SerializeMethod>();
  }

  /**
   * Registers a new SerializeMethod with this SerializerHelper.
   *
   * @param method the method that should be registered.
   */
  public synchronized void registerMethod( final SerializeMethod method ) {
    this.methods.put( method.getObjectClass(), method );
  }

  /**
   * Traverses the configuration and registers all serialization handlers in this factory.
   */
  protected void registerMethods() {
    final Configuration config = LibSerializerBoot.getInstance().getGlobalConfig();
    final Iterator sit = config.findPropertyKeys( "org.pentaho.reporting.libraries.serializer.handler." );

    while ( sit.hasNext() ) {
      final String configkey = (String) sit.next();
      final String c = config.getConfigProperty( configkey );
      final SerializeMethod maybeModule = ObjectUtilities.loadAndInstantiate
        ( c, SerializerHelper.class, SerializeMethod.class );
      if ( maybeModule != null ) {
        registerMethod( maybeModule );
      } else {
        logger.warn( "Invalid SerializeMethod implementation: " + c );
      }
    }
  }

  /**
   * Deregisters a new SerializeMethod with this SerializerHelper.
   *
   * @param method the method that should be deregistered.
   */
  public synchronized void unregisterMethod( final SerializeMethod method ) {
    this.methods.remove( method.getObjectClass() );
  }

  /**
   * Returns the collection of all registered serialize methods.
   *
   * @return a collection of the registered serialize methods.
   */
  protected HashMap getMethods() {
    return methods;
  }

  /**
   * Returns the class comparator instance used to find correct super classes.
   *
   * @return the class comparator.
   */
  protected ClassComparator getComparator() {
    return comparator;
  }

  /**
   * Looks up the SerializeMethod for the given class or null if there is no SerializeMethod for the given class.
   *
   * @param c the class for which we want to lookup a serialize method.
   * @return the method or null, if there is no registered method for the class.
   */
  protected SerializeMethod getSerializer( final Class c ) {
    final SerializeMethod sm = methods.get( c );
    if ( sm != null ) {
      return sm;
    }
    return getSuperClassObjectDescription( c );
  }

  /**
   * Looks up the SerializeMethod for the given class or null if there is no SerializeMethod for the given class. This
   * method searches all superclasses.
   *
   * @param d the class for which we want to lookup a serialize method.
   * @return the method or null, if there is no registered method for the class.
   */
  @SuppressWarnings( "unchecked" )
  protected SerializeMethod getSuperClassObjectDescription
  ( final Class d ) {
    SerializeMethod knownSuperClass = null;
    final Iterator<Class> keys = methods.keySet().iterator();
    while ( keys.hasNext() ) {
      final Class keyClass = keys.next();
      if ( keyClass.isAssignableFrom( d ) ) {
        final SerializeMethod od = methods.get( keyClass );
        if ( knownSuperClass == null ) {
          knownSuperClass = od;
        } else {
          if ( comparator.isComparable
            ( knownSuperClass.getObjectClass(), od.getObjectClass() ) ) {
            if ( comparator.compare
              ( knownSuperClass.getObjectClass(), od.getObjectClass() ) < 0 ) {
              knownSuperClass = od;
            }
          }
        }
      }
    }
    return knownSuperClass;
  }


  /**
   * Writes a serializable object description to the given object output stream. This method selects the best serialize
   * helper method for the given object.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public synchronized void writeObject( final Object o,
                                        final ObjectOutputStream out )
    throws IOException {
    try {
      if ( o == null ) {
        out.writeByte( 0 );
        return;
      }
      if ( o instanceof Serializable ) {
        out.writeByte( 1 );
        out.writeObject( o );
        return;
      }

      final SerializeMethod m = getSerializer( o.getClass() );
      if ( m == null ) {
        throw new NotSerializableException( o.getClass().getName() );
      }
      out.writeByte( 2 );
      out.writeObject( m.getObjectClass() );
      m.writeObject( o, out );
    } catch ( NotSerializableException nse ) {
      logger.warn( "Unable to serialize object: " + o );
      throw nse;
    }
  }

  public synchronized boolean isSerializable( final Object o ) {
    if ( o == null ) {
      return true;
    }
    if ( o instanceof Serializable ) {
      return true;
    }

    final SerializeMethod m = getSerializer( o.getClass() );
    return m != null;
  }

  /**
   * Reads the object from the object input stream. This object selects the best serializer to read the object.
   * <p/>
   * Make sure, that you use the same configuration (library and class versions, registered methods in the
   * SerializerHelper) for reading as you used for writing.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public synchronized Object readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    final int type = in.readByte();
    if ( type == 0 ) {
      return null;
    }
    if ( type == 1 ) {
      return in.readObject();
    }
    final Class c = (Class) in.readObject();
    final SerializeMethod m = getSerializer( c );
    if ( m == null ) {
      throw new NotSerializableException( c.getName() );
    }
    return m.readObject( in );
  }
}
