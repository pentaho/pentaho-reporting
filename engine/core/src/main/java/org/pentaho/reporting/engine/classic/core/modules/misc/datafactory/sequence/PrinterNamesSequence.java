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
