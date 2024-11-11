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

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * A SerializeMethod implementation that handles PageFormat objects.
 *
 * @author Thomas Morgner
 * @see java.awt.print.PageFormat
 */
public class PageFormatSerializer implements SerializeMethod {
  /**
   * Default Constructor.
   */
  public PageFormatSerializer() {
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
    final PageFormat pf = (PageFormat) o;
    out.writeObject( resolvePageFormat( pf ) );
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
    final Object[] pageFormatResolve = (Object[]) in.readObject();
    return createPageFormat( pageFormatResolve );
  }

  /**
   * Returns the class of the object, which this object can serialize.
   *
   * @return the class of java.awt.print.PageFormat.
   */
  public Class getObjectClass() {
    return PageFormat.class;
  }


  /**
   * Resolves a page format, so that the result can be serialized.
   *
   * @param format the page format that should be prepared for serialisation.
   * @return the prepared page format data.
   */
  private Object[] resolvePageFormat( final PageFormat format ) {
    final Integer orientation = new Integer( format.getOrientation() );
    final Paper p = format.getPaper();
    final float[] fdim = new float[] { (float) p.getWidth(), (float) p.getHeight() };
    final float[] rect = new float[] { (float) p.getImageableX(),
      (float) p.getImageableY(),
      (float) p.getImageableWidth(),
      (float) p.getImageableHeight() };
    return new Object[] { orientation, fdim, rect };
  }

  /**
   * Restores a page format after it has been serialized.
   *
   * @param data the serialized page format data.
   * @return the restored page format.
   */
  private PageFormat createPageFormat( final Object[] data ) {
    final Integer orientation = (Integer) data[ 0 ];
    final float[] dim = (float[]) data[ 1 ];
    final float[] rect = (float[]) data[ 2 ];
    final Paper p = new Paper();
    p.setSize( dim[ 0 ], dim[ 1 ] );
    p.setImageableArea( rect[ 0 ], rect[ 1 ], rect[ 2 ], rect[ 3 ] );
    final PageFormat format = new PageFormat();
    format.setPaper( p );
    format.setOrientation( orientation.intValue() );
    return format;
  }
}
