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

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.PrinterName;
import javax.swing.table.TableModel;

public class PrinterNamesSequence extends AbstractSequence {
  public PrinterNamesSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new PrinterNamesSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final PrintService[] services = PrintServiceLookup.lookupPrintServices( DocFlavor.SERVICE_FORMATTED.PAGEABLE, null );
    final TypedTableModel tt = new TypedTableModel();
    tt.addColumn( "Printer ID", String.class );
    tt.addColumn( "Printer Name", String.class );
    for ( int i = 0; i < services.length; i++ ) {
      final PrintService service = services[i];
      final PrinterName displayName = service.getAttribute( PrinterName.class );
      if ( displayName != null ) {
        tt.addRow( new Object[] { service.getName(), displayName.getValue() } );
      } else {
        tt.addRow( new Object[] { service.getName(), service.getName() } );
      }
    }
    return tt;
  }
}
