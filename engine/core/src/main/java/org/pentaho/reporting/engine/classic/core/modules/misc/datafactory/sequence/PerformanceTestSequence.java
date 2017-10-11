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
