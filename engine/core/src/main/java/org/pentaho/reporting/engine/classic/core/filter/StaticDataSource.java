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

package org.pentaho.reporting.engine.classic.core.filter;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A data source that returns a constant value. An example is a label on a report.
 *
 * @author Thomas Morgner
 */
public class StaticDataSource implements DataSource {
  /**
   * The value.
   */
  private transient Object value;

  /**
   * Default constructor.
   */
  public StaticDataSource() {
  }

  /**
   * Constructs a new static data source.
   *
   * @param o
   *          The value.
   */
  public StaticDataSource( final Object o ) {
    setValue( o );
  }

  /**
   * Sets the value of the data source.
   *
   * @param o
   *          The value.
   */
  public void setValue( final Object o ) {
    this.value = o;
  }

  /**
   * Returns the value set in this datasource. This method exists to make the value-property beanified.
   *
   * @return the value.
   */
  public Object getValue() {
    return value;
  }

  /**
   * Returns the value of the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return The value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return value;
  }

  /**
   * Clones the data source, although the enclosed 'static' value is not cloned.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public StaticDataSource clone() throws CloneNotSupportedException {
    return (StaticDataSource) super.clone();
  }

  /**
   * Helper method for serialization.
   *
   * @param out
   *          the output stream where to write the object.
   * @throws IOException
   *           if errors occur while writing the stream.
   */
  private void writeObject( final ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
    SerializerHelper.getInstance().writeObject( value, out );
  }

  /**
   * Helper method for serialization.
   *
   * @param in
   *          the input stream from where to read the serialized object.
   * @throws IOException
   *           when reading the stream fails.
   * @throws ClassNotFoundException
   *           if a class definition for a serialized object could not be found.
   */
  private void readObject( final java.io.ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    value = SerializerHelper.getInstance().readObject( in );
  }
}
