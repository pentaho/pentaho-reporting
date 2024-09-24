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

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SerializeMethod for GradientPaint objects.
 *
 * @author Thomas Morgner
 */
public class GradientPaintSerializer implements SerializeMethod {
  /**
   * Default constructor.
   */
  public GradientPaintSerializer() {
  }

  /**
   * Writes a serializable object description to the given object output stream.
   *
   * @param o      the to be serialized object.
   * @param stream the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public void writeObject( final Object o, final ObjectOutputStream stream ) throws IOException {
    final GradientPaint gp = (GradientPaint) o;
    final Point2D point2D1 = gp.getPoint1();
    stream.writeFloat( (float) point2D1.getX() );
    stream.writeFloat( (float) point2D1.getY() );
    stream.writeObject( gp.getColor1() );
    final Point2D point2D = gp.getPoint2();
    stream.writeFloat( (float) point2D.getX() );
    stream.writeFloat( (float) point2D.getY() );
    stream.writeObject( gp.getColor2() );
    stream.writeBoolean( gp.isCyclic() );
  }

  /**
   * Reads the object from the object input stream.
   *
   * @param stream the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject( final ObjectInputStream stream )
    throws IOException, ClassNotFoundException {
    final float x1 = stream.readFloat();
    final float y1 = stream.readFloat();
    final Color c1 = (Color) stream.readObject();
    final float x2 = stream.readFloat();
    final float y2 = stream.readFloat();
    final Color c2 = (Color) stream.readObject();
    final boolean isCyclic = stream.readBoolean();
    return new GradientPaint( x1, y1, c1, x2, y2, c2, isCyclic );
  }

  /**
   * The class of the object, which this object can serialize.
   *
   * @return the class of the object type, which this method handles.
   */
  public Class getObjectClass() {
    return GradientPaint.class;
  }
}
