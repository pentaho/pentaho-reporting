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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.serializer.methods;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pentaho.reporting.libraries.serializer.SerializeMethod;

/**
 * A SerializeMethod implementation that handles Line2D objects.
 *
 * @author Thomas Morgner
 * @see java.awt.geom.Line2D
 */
public class Line2DSerializer implements SerializeMethod
{
  /**
   * Default Constructor.
   */
  public Line2DSerializer ()
  {
  }

  /**
   * Writes a serializable object description to the given object output stream.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public void writeObject (final Object o, final ObjectOutputStream out)
          throws IOException
  {
    final Line2D line = (Line2D) o;
    out.writeDouble(line.getX1());
    out.writeDouble(line.getY1());
    out.writeDouble(line.getX2());
    out.writeDouble(line.getY2());
  }

  /**
   * Reads the object from the object input stream.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   *
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject (final ObjectInputStream in)
          throws IOException, ClassNotFoundException
  {
    final double x1 = in.readDouble();
    final double y1 = in.readDouble();
    final double x2 = in.readDouble();
    final double y2 = in.readDouble();
    return new Line2D.Double(x1, y1, x2, y2);
  }

  /**
   * Returns the class of the object, which this object can serialize.
   *
   * @return the class of java.awt.geom.Line2D.
   */
  public Class getObjectClass ()
  {
    return Line2D.class;
  }
}
