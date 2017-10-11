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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager;
import org.pentaho.reporting.engine.classic.core.layout.StackedLayoutManager;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Creation-Date: Dec 17, 2006, 2:36:55 PM
 *
 * @author Thomas Morgner
 */
public class ElementStyleSheetObjectDescription implements ObjectDescription {
  private StyleKeyFactory keyfactory;
  private ElementStyleSheet styleSheet;

  public ElementStyleSheetObjectDescription() {
  }

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( final Configuration config ) {
  }

  public void init( final RootXmlReadHandler rootHandler, final ElementStyleSheet styleSheet ) {
    this.keyfactory = (StyleKeyFactory) rootHandler.getHelperObject( ReportDefinitionReadHandler.STYLE_FACTORY_KEY );
    this.styleSheet = styleSheet;

  }

  /**
   * Creates an object based on the description.
   *
   * @return The object.
   */
  public Object createObject() {
    return styleSheet;
  }

  /**
   * Returns a cloned instance of the object description. The contents of the parameter objects collection are cloned
   * too, so that any already defined parameter value is copied to the new instance.
   * <p/>
   * Parameter definitions are not cloned, as they are considered read-only.
   *
   * @return A cloned instance.
   */
  public ObjectDescription getInstance() {
    throw new UnsupportedOperationException( "This is a private factory, go away." );
  }

  /**
   * Returns the object class.
   *
   * @return The Class.
   */
  public Class getObjectClass() {
    return ElementStyleSheet.class;
  }

  /**
   * Returns the value of a parameter.
   *
   * @param name
   *          the parameter name.
   * @return The value.
   */
  public Object getParameter( final String name ) {
    final StyleKey key = keyfactory.getStyleKey( name );
    if ( key == null ) {
      throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
    }
    return styleSheet.getStyleProperty( key );
  }

  /**
   * Returns a parameter definition. If the parameter is invalid, this function returns null.
   *
   * @param name
   *          the definition name.
   * @return The parameter class or null, if the parameter is not defined.
   */
  public Class getParameterDefinition( final String name ) {
    if ( "layoutmanager".equals( name ) ) {
      return BandLayoutManager.class;
    }
    if ( "absolute_pos".equals( name ) ) {
      return Point2D.class;
    }
    if ( "border-top-left-radius".equals( name ) || "border-top-right-radius".equals( name )
        || "border-bottom-left-radius".equals( name ) || "border-bottom-right-radius".equals( name )
        || "min-size".equals( name ) || "max-size".equals( name ) || "preferred-size".equals( name ) ) {
      return Dimension2D.class;
    }

    final StyleKey key = keyfactory.getStyleKey( name );
    if ( key == null ) {
      throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
    }
    return key.getValueType();
  }

  /**
   * Returns an iterator the provides access to the parameter names. This returns all _known_ parameter names, the
   * object description may accept additional parameters.
   *
   * @return The iterator.
   */
  public Iterator getParameterNames() {
    // don't say anything ...
    return new ArrayList().iterator();
  }

  /**
   * Returns a cloned instance of the object description. The contents of the parameter objects collection are cloned
   * too, so that any already defined parameter value is copied to the new instance.
   * <p/>
   * Parameter definitions are not cloned, as they are considered read-only.
   * <p/>
   * The newly instantiated object description is not configured. If it need to be configured, then you have to call
   * configure on it.
   *
   * @return A cloned instance.
   */
  public ObjectDescription getUnconfiguredInstance() {
    throw new UnsupportedOperationException( "This is a private factory, go away." );
  }

  /**
   * Sets the value of a parameter.
   *
   * @param name
   *          the parameter name.
   * @param value
   *          the parameter value.
   */
  public void setParameter( final String name, final Object value ) {
    if ( "layoutmanager".equals( name ) ) {
      if ( value instanceof StackedLayoutManager ) {
        styleSheet.setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_BLOCK );
      } else {
        styleSheet.setStyleProperty( BandStyleKeys.LAYOUT, null );
      }
      return;
    }
    if ( "border-top-right-radius".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "border-top-left-radius".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "border-bottom-right-radius".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "border-bottom-left-radius".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "absolute_pos".equals( name ) ) {
      if ( value instanceof Point2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Point2D d = (Point2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( d.getX() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( d.getY() ) );
      return;
    }
    if ( "min-size".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "max-size".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.MAX_WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.MAX_HEIGHT, new Float( d.getHeight() ) );
      return;
    }
    if ( "preferred-size".equals( name ) ) {
      if ( value instanceof Dimension2D == false ) {
        throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
      }
      final Dimension2D d = (Dimension2D) value;
      styleSheet.setStyleProperty( ElementStyleKeys.WIDTH, new Float( d.getWidth() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.HEIGHT, new Float( d.getHeight() ) );
      return;
    }

    final StyleKey key = keyfactory.getStyleKey( name );
    if ( key == null ) {
      throw new IllegalArgumentException( "There is no handler for the stylekey: " + name );
    }
    styleSheet.setStyleProperty( key, value );
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    throw new UnsupportedOperationException( "This is a private factory, go away." );
  }
}
