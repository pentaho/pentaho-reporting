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
