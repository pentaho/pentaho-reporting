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
