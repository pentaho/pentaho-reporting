/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

public class StaticDataFactorySample {
  private static final Integer ZERO = new Integer( 0 );

  public StaticDataFactorySample() {
  }

  public TableModel createMainQuery() {
    final TypedTableModel model =
        new TypedTableModel( new String[] { "ID", "TEXT" }, new Class[] { Integer.class, String.class }, 0 );
    model.addRow( new Object[] { new Integer( 0 ), "Hello World" } );
    model.addRow( new Object[] { new Integer( 1 ), "Your DataFactory works perfectly." } );
    return model;
  }

  public TableModel createSubQuery( Integer parameter ) {
    final TypedTableModel model =
        new TypedTableModel( new String[] { "ID", "NUMBER", "DESCRIPTION" }, new Class[] { Integer.class, String.class,
          String.class }, 0 );
    if ( ZERO.equals( parameter ) ) {
      model.addRow( new Object[] { parameter, new Integer( 0 ), "Look, you got a new dataset." } );
      model.addRow( new Object[] { parameter, new Integer( 1 ), "So Subreport queries work too.." } );
      return model;
    } else {
      model.addRow( new Object[] { parameter, new Integer( 0 ), "Ahh, another query-parameter, another table." } );
      model.addRow( new Object[] { parameter, new Integer( 1 ),
        "Subreports can use parameters to control what data is returned." } );
      return model;
    }

  }

}
