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

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;
import java.util.Map;

public class SequenceDataFactory extends AbstractDataFactory {
  private LinkedHashMap<String, Sequence> sequences;

  public SequenceDataFactory() {
    sequences = new LinkedHashMap<String, Sequence>();
  }

  public void addSequence( final String query, final Sequence sequence ) {
    sequences.put( query, sequence );
  }

  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final Sequence sequence = sequences.get( query );
    if ( sequence == null ) {
      throw new ReportDataFactoryException( "No such query '" + query + "'" );
    }
    return sequence.produce( parameters, getDataFactoryContext() );
  }

  public void close() {

  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return sequences.containsKey( query );
  }

  public String[] getQueryNames() {
    return sequences.keySet().toArray( new String[sequences.size()] );
  }

  public SequenceDataFactory clone() {
    final SequenceDataFactory dataFactory = (SequenceDataFactory) super.clone();
    dataFactory.sequences = (LinkedHashMap<String, Sequence>) sequences.clone();
    for ( final Map.Entry<String, Sequence> entry : dataFactory.sequences.entrySet() ) {
      final Sequence value = entry.getValue();
      entry.setValue( (Sequence) value.clone() );
    }
    return dataFactory;
  }

  public Sequence getSequence( final String name ) {
    return sequences.get( name );
  }
}
