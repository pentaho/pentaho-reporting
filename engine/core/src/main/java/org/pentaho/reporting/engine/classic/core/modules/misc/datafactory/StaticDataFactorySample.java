/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
