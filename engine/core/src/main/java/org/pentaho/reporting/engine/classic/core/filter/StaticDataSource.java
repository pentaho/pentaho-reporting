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
