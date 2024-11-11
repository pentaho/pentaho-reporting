/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.serializer.methods;

import org.pentaho.reporting.libraries.serializer.SerializeMethod;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A SerializeMethod implementation that handles Rectangle2D objects.
 *
 * @author Thomas Morgner
 * @see java.awt.geom.Rectangle2D
 */
public class Rectangle2DSerializer implements SerializeMethod {
  /**
   * Default Constructor.
   */
  public Rectangle2DSerializer() {
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
    final Rectangle2D rectangle = (Rectangle2D) o;
    out.writeDouble( rectangle.getX() );
    out.writeDouble( rectangle.getY() );
    out.writeDouble( rectangle.getWidth() );
    out.writeDouble( rectangle.getHeight() );
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
    return new Rectangle2D.Double( x, y, w, h );
  }

  /**
   * Returns the class of the object, which this object can serialize.
   *
   * @return the class of java.awt.geom.Rectangle2D.
   */
  public Class getObjectClass() {
    return Rectangle2D.class;
  }
}
