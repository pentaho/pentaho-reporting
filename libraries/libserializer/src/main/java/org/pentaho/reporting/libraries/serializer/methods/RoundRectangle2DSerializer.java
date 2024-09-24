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

import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A SerializeMethod implementation that handles Rectangle2D objects.
 *
 * @author Thomas Morgner
 * @see Rectangle2D
 */
public class RoundRectangle2DSerializer implements SerializeMethod {
  /**
   * Default Constructor.
   */
  public RoundRectangle2DSerializer() {
  }

  /**
   * Writes a serializable object description to the given object output stream.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public void writeObject( final Object o, final ObjectOutputStream out )
    throws IOException {
    final RoundRectangle2D rectangle = (RoundRectangle2D) o;
    out.writeDouble( rectangle.getX() );
    out.writeDouble( rectangle.getY() );
    out.writeDouble( rectangle.getWidth() );
    out.writeDouble( rectangle.getHeight() );
    out.writeDouble( rectangle.getArcWidth() );
    out.writeDouble( rectangle.getArcHeight() );
  }

  /**
   * Reads the object from the object input stream.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    final double x = in.readDouble();
    final double y = in.readDouble();
    final double w = in.readDouble();
    final double h = in.readDouble();
    final double aw = in.readDouble();
    final double ah = in.readDouble();
    return new RoundRectangle2D.Double( x, y, w, h, aw, ah );
  }

  /**
   * Returns the class of the object, which this object can serialize.
   *
   * @return the class of java.awt.geom.Rectangle2D.
   */
  public Class getObjectClass() {
    return RoundRectangle2D.class;
  }
}
