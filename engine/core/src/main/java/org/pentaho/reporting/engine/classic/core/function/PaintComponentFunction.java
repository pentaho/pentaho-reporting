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

package org.pentaho.reporting.engine.classic.core.function;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * Paints a AWT or Swing Component, fitting the component into the element bounds. The component must be contained in
 * the dataRow.
 * <p/>
 * In an headless environment this function wont work and will always return null.
 *
 * @author Thomas Morgner
 * @deprecated Use the new Component-Element instead. It uses drawables for this job, and therefore the result looks
 *             much better. This method does no longer work, as it depended on implementation details that are no longer
 *             in use.
 */
@SuppressWarnings( "deprecation" )
public class PaintComponentFunction extends AbstractFunction {
  private static final Log logger = LogFactory.getLog( PaintComponentFunction.class );

  /**
   * Supplies a valid AWT-peer for the draw operation.
   */
  private transient Frame peerSupply;
  /**
   * The name of the report element that should receive the image.
   */
  private String element;

  /**
   * The field from where to read the AWT-Component.
   */
  private String field;

  /**
   * The scale factor.
   */
  private float scale;

  /**
   * DefaultConstructor.
   */
  public PaintComponentFunction() {
    if ( PaintComponentFunction.isHeadless() == false ) {
      peerSupply = new Frame();
      peerSupply.setLayout( new BorderLayout() );
    }
    this.scale = 1;
  }

  /**
   * Returns the element used by the function.
   * <P>
   * The element name corresponds to a element in the report. The element name must be unique, as the first occurence of
   * the element is used.
   *
   * @return The field name.
   */
  public String getElement() {
    return element;
  }

  /**
   * Sets the element name for the function.
   * <P>
   * The element name corresponds to a element in the report. The element name must be unique, as the first occurence of
   * the element is used.
   *
   * @param field
   *          the field name (null not permitted).
   */
  public void setElement( final String field ) {
    if ( field == null ) {
      throw new NullPointerException();
    }
    this.element = field;
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Tests, whether the report generation is executed in an headless environment.
   *
   * @return true, if this is an headless environment, false otherwise.
   */
  protected static boolean isHeadless() {
    return "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( "java.awt.headless",
        "false" ) );
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return null;
  }

  /**
   * Define a scale factor for the created image. Using a higher scale factor will produce better results. A scale
   * factor of 2 will double the resolution. A scale factor of 1 will create 72 dpi images.
   *
   * @param scale
   *          the scale factor.
   */
  public void setScale( final float scale ) {
    this.scale = scale;
  }

  /**
   * Gets the scale factor for the created image. Using a higher scale factor will produce better results. A scale
   * factor of 2 will double the resolution. A scale factor of 1 will create 72 dpi images.
   *
   * @return the scale factor.
   */
  public float getScale() {
    return scale;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final PaintComponentFunction pc = (PaintComponentFunction) super.getInstance();
    if ( PaintComponentFunction.isHeadless() == false ) {
      pc.peerSupply = new Frame();
      pc.peerSupply.setLayout( new BorderLayout() );
    }
    return pc;
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
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    if ( PaintComponentFunction.isHeadless() == false ) {
      peerSupply = new Frame();
      peerSupply.setLayout( new BorderLayout() );
    }
  }
}
