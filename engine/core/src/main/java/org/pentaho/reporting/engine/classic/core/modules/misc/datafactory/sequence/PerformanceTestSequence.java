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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class PerformanceTestSequence extends AbstractSequence {
  public PerformanceTestSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new PerformanceTestSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final int limit = getTypedParameter( "limit", Integer.class, 100 );
    final long seed = getTypedParameter( "seed", Long.class, System.currentTimeMillis() );

    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "rowcount", Integer.class );
    model.addColumn( "integer", Integer.class );
    model.addColumn( "double", Double.class );
    model.addColumn( "text", String.class );
    model.addColumn( "text2", String.class );
    model.addColumn( "date", Date.class );

    final Random random = new Random();
    random.setSeed( seed );

    final Calendar baseDate = new GregorianCalendar( 2000, 1, 1 );
    baseDate.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
    final long millis = baseDate.getTimeInMillis();
    for ( int i = 0; i < limit; i++ ) {
      model.addRow( i, (int) ( random.nextDouble() * Integer.MAX_VALUE ) - ( Integer.MAX_VALUE / 2 ), random
          .nextDouble()
          * Integer.MAX_VALUE, "Some Text with breaks " + i, "SomeTextWithoutBreaks" + i, new Date( millis
            + (long) ( 200 * random.nextDouble() * Integer.MAX_VALUE ) ) );
    }

    return model;
  }
}
