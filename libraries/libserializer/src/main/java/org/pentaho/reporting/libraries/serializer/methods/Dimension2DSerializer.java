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

import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.serializer.SerializeMethod;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A SerializeMethod implementation that handles Dimension2D objects.
 *
 * @author Thomas Morgner
 * @see java.awt.geom.Dimension2D
 */
public class Dimension2DSerializer implements SerializeMethod {
  /**
   * Default Constructor.
   */
  public Dimension2DSerializer() {
  }

  /**
   * Writes a serializable object description to the given object output stream. This method writes the width and the
   * height of the dimension into the stream.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public void writeObject( final Object o, final ObjectOutputStream out )
    throws IOException {
    final Dimension2D dim = (Dimension2D) o;
    out.writeDouble( dim.getWidth() );
    out.writeDouble( dim.getHeight() );
  }

  /**
   * Reads the object from the object input stream. This read the width and height and constructs a new FloatDimension
   * object.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    final double w = in.readDouble();
    final double h = in.readDouble();
    return new FloatDimension( (float) w, (float) h );
  }

  /**
   * Returns the class of the object, which this object can serialize.
   *
   * @return the class of java.awt.geom.Dimension2D.
   */
  public Class getObjectClass() {
    return Dimension2D.class;
  }
}
