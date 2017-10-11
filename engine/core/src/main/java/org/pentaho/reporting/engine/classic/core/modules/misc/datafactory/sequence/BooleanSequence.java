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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

public class BooleanSequence extends AbstractSequence {
  public BooleanSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new BooleanSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final Boolean trueFirst = getTypedParameter( "true-as-first", Boolean.class, Boolean.TRUE );
    final String trueText = getTypedParameter( "true-text", String.class, "true" );
    final String falseText = getTypedParameter( "false-text", String.class, "false" );

    final TypedTableModel tableModel = new TypedTableModel();
    tableModel.addColumn( "bool", Boolean.class );
    tableModel.addColumn( "int", Integer.class );
    tableModel.addColumn( "text", String.class );

    if ( Boolean.TRUE.equals( trueFirst ) ) {
      tableModel.setValueAt( Boolean.TRUE, 0, 0 );
      tableModel.setValueAt( 1, 0, 1 );
      tableModel.setValueAt( trueText, 0, 2 );

      tableModel.setValueAt( Boolean.FALSE, 1, 0 );
      tableModel.setValueAt( 0, 1, 1 );
      tableModel.setValueAt( falseText, 1, 2 );
    } else {
      tableModel.setValueAt( Boolean.FALSE, 0, 0 );
      tableModel.setValueAt( 0, 0, 1 );
      tableModel.setValueAt( falseText, 0, 2 );

      tableModel.setValueAt( Boolean.TRUE, 1, 0 );
      tableModel.setValueAt( 1, 1, 1 );
      tableModel.setValueAt( trueText, 1, 2 );
    }

    return tableModel;
  }
}
