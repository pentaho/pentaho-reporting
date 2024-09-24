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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

/**
 * Creation-Date: Jan 18, 2007, 5:59:40 PM
 *
 * @author Thomas Morgner
 */
public class StaticDataSourceFactoryTestSupport extends TypedTableModel {
  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of zero columns and zero rows.
   */
  public StaticDataSourceFactoryTestSupport() {
    addColumn( "Call", String.class );
    addColumn( "ConstructorParam1", String.class );
    addColumn( "ConstructorParam2", Integer.class );
    setValueAt( "StaticDataSourceFactoryTestSupport()", 0, 0 );
  }

  public StaticDataSourceFactoryTestSupport( String parameter, int parameter2 ) {
    this();
    if ( "test".equals( parameter ) == false || parameter2 != 5 ) {
      throw new IllegalStateException();
    }
    setValueAt( "StaticDataSourceFactoryTestSupport(String parameter, int parameter2)", 0, 0 );
    setValueAt( parameter, 0, 1 );
    setValueAt( parameter2, 0, 2 );
  }

  public TableModel createParametrizedTableModel( int i1, String s1 ) {
    TestCase.assertEquals( "Passing primitive parameters failed", 5, i1 );
    TestCase.assertEquals( "Passing object parameters failed", "test", s1 );
    addColumn( "CallParam1", Integer.class );
    addColumn( "CallParam2", String.class );
    setValueAt( "StaticDataSourceFactoryTestSupport#createParametrizedTableModel", 0, 0 );
    setValueAt( i1, 0, 3 );
    setValueAt( s1, 0, 4 );
    return this;
  }

  public TableModel createSimpleTableModel() {
    setValueAt( "StaticDataSourceFactoryTestSupport#createSimpleTableModel", 0, 0 );
    return this;
  }

}
