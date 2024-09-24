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

import java.awt.Component;

import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.ComponentDrawable;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * A filter that wraps AWT- and Swing-components into a Drawable implementation.
 *
 * @author Thomas Morgner
 */
public class ComponentDrawableFilter implements DataFilter {
  /**
   * The datasource from where to read the urls.
   */
  private DataSource source;
  /**
   * The Window-Peer used for the draw operation.
   */
  private JFrame frame;

  /**
   * Default constructor.
   */
  public ComponentDrawableFilter() {
  }

  /**
   * Returns the ComponentDrawable for the AWT-Component or null.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( ComponentDrawableFilter.isHeadless() ) {
      return null;
    }

    if ( runtime == null ) {
      return null;
    }

    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return null;
    }
    final Object o = ds.getValue( runtime, element );
    if ( o == null ) {
      return null;
    }

    if ( o instanceof Component == false ) {
      return null;
    }

    final Configuration config = runtime.getConfiguration();
    final ComponentDrawable cd;
    final String drawMode =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.ComponentDrawableMode", "shared" );
    if ( "private".equals( drawMode ) ) {
      cd = new ComponentDrawable();
    } else if ( "synchronized".equals( drawMode ) ) {
      cd = new ComponentDrawable();
      cd.setPaintSynchronized( true );
    } else {
      if ( frame == null ) {
        frame = new JFrame();
      }
      cd = new ComponentDrawable( frame );
      cd.setPaintSynchronized( true );
    }

    final String allowOwnPeer =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.AllowOwnPeerForComponentDrawable" );
    cd.setAllowOwnPeer( "true".equals( allowOwnPeer ) );
    cd.setComponent( (Component) o );
    return cd;
  }

  /**
   * A helper method that queries the configuration (and therefore also the system properties) for whether the system is
   * in headless mode.
   *
   * @return true, if the system is headless, false otherwise.
   */
  protected static boolean isHeadless() {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    return "true".equals( config.getConfigProperty( "java.awt.headless", "false" ) );
  }

  /**
   * Creates a clone of this filter.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           if an error occured.
   */
  public ComponentDrawableFilter clone() throws CloneNotSupportedException {
    final ComponentDrawableFilter il = (ComponentDrawableFilter) super.clone();
    if ( source != null ) {
      il.source = source.clone();
    }
    return il;
  }

  /**
   * Returns the assigned DataSource for this Target.
   *
   * @return The datasource.
   */
  public DataSource getDataSource() {
    return source;
  }

  /**
   * Assigns a DataSource for this Target.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( final DataSource ds ) {
    this.source = ds;
  }

}
