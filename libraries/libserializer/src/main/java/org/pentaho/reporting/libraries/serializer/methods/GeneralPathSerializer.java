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

package org.pentaho.reporting.libraries.serializer.methods;

import org.pentaho.reporting.libraries.serializer.SerializeMethod;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A serialize method that handles java.awt.geom.GeneralPath objects.
 *
 * @author Thomas Morgner
 */
public class GeneralPathSerializer implements SerializeMethod {
  /**
   * Default constructor.
   */
  public GeneralPathSerializer() {
  }

  /**
   * The class of the object, which this object can serialize.
   *
   * @return the class of the object type, which this method handles.
   */
  public Class getObjectClass() {
    return GeneralPath.class;
  }

  /**
   * Reads the object from the object input stream.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws java.io.IOException    if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    final int winding = in.readInt();
    final GeneralPath gp = new GeneralPath( winding );

    // type will be -1 at the end of the GPath ..
    int type = in.readInt();
    while ( type >= 0 ) {
      switch( type ) {
        case PathIterator.SEG_MOVETO: {
          final float x = in.readFloat();
          final float y = in.readFloat();
          gp.moveTo( x, y );
          break;
        }
        case PathIterator.SEG_LINETO: {
          final float x = in.readFloat();
          final float y = in.readFloat();
          gp.lineTo( x, y );
          break;
        }
        case PathIterator.SEG_QUADTO: {
          final float x1 = in.readFloat();
          final float y1 = in.readFloat();
          final float x2 = in.readFloat();
          final float y2 = in.readFloat();
          gp.quadTo( x1, y1, x2, y2 );
          break;
        }
        case PathIterator.SEG_CUBICTO: {
          final float x1 = in.readFloat();
          final float y1 = in.readFloat();
          final float x2 = in.readFloat();
          final float y2 = in.readFloat();
          final float x3 = in.readFloat();
          final float y3 = in.readFloat();
          gp.curveTo( x1, y1, x2, y2, x3, y3 );
          break;
        }
        case PathIterator.SEG_CLOSE: {
          break;
        }
        default:
          throw new IOException( "Unexpected type encountered: " + type );
      }
      type = in.readInt();
    }
    return gp;
  }

  /**
   * Writes a serializable object description to the given object output stream.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws java.io.IOException if an I/O error occured.
   */
  public void writeObject( final Object o, final ObjectOutputStream out )
    throws IOException {
    final GeneralPath gp = (GeneralPath) o;
    final PathIterator it = gp.getPathIterator( new AffineTransform() );
    out.writeInt( it.getWindingRule() );
    while ( it.isDone() == false ) {
      final float[] corrds = new float[ 6 ];
      final int type = it.currentSegment( corrds );
      out.writeInt( type );

      switch( type ) {
        case PathIterator.SEG_MOVETO: {
          out.writeFloat( corrds[ 0 ] );
          out.writeFloat( corrds[ 1 ] );
          break;
        }
        case PathIterator.SEG_LINETO: {
          out.writeFloat( corrds[ 0 ] );
          out.writeFloat( corrds[ 1 ] );
          break;
        }
        case PathIterator.SEG_QUADTO: {
          out.writeFloat( corrds[ 0 ] );
          out.writeFloat( corrds[ 1 ] );
          out.writeFloat( corrds[ 2 ] );
          out.writeFloat( corrds[ 3 ] );
          break;
        }
        case PathIterator.SEG_CUBICTO: {
          out.writeFloat( corrds[ 0 ] );
          out.writeFloat( corrds[ 1 ] );
          out.writeFloat( corrds[ 2 ] );
          out.writeFloat( corrds[ 3 ] );
          out.writeFloat( corrds[ 4 ] );
          out.writeFloat( corrds[ 5 ] );
          break;
        }
        case PathIterator.SEG_CLOSE: {
          break;
        }
        default:
          throw new IOException( "Unexpected type encountered: " + type );
      }
      it.next();
    }
    out.writeInt( -1 );
  }
}
