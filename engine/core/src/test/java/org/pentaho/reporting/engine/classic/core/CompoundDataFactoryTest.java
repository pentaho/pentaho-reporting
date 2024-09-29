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


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Arrays;

public class CompoundDataFactoryTest extends TestCase {
  private static class MockDataFactory extends AbstractDataFactory {
    private static final long serialVersionUID = 1L;
    private String[] queryNames;
    private boolean designTimeCalled;

    public MockDataFactory( final String... queryNames ) {
      this.queryNames = queryNames;
    }

    public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
      return new DefaultTableModel();
    }

    public DataFactory derive() {
      return this;
    }

    public void close() {
    }

    public TableModel queryDesignTimeStructure( final String query, final DataRow parameter )
      throws ReportDataFactoryException {
      designTimeCalled = true;
      return super.queryDesignTimeStructure( query, parameter );
    }

    public boolean isDesignTimeCalled() {
      return designTimeCalled;
    }

    public boolean isQueryExecutable( final String query, final DataRow parameters ) {
      return queryNames != null && Arrays.binarySearch( queryNames, query ) != -1;
    }

    public String[] getQueryNames() {
      return queryNames;
    }

    @Override
    public MockDataFactory clone() {
      return new MockDataFactory( queryNames );
    }
  }

  public void testGetDataFactoryForName_no_metadata() {
    try {
      final String queryName = "test"; //$NON-NLS-1$
      final CompoundDataFactory cdf = new CompoundDataFactory();
      cdf.add( new MockDataFactory( queryName ) );

      assertFalse( DataFactoryRegistry.getInstance().isRegistered( MockDataFactory.class.getName() ) );
      assertEquals( 1, cdf.getQueryNames().length );
      cdf.getDataFactoryForQuery( queryName );
      fail();
    } catch ( MetaDataLookupException e ) {
      // we no longer allow data-factories without metadata in the system.
      // e.printStackTrace();
    }

  }

  public void testGetDataFactoryForName_freeform() {
    final DefaultDataFactoryMetaData metadata = new DefaultDataFactoryMetaData( MockDataFactory.class.getName(), "", //$NON-NLS-1$
        "", //$NON-NLS-1$
        false, false, false, false, false, true, // freeform
        false, MaturityLevel.Limited, new DefaultDataFactoryCore(), -1 );

    try {
      final String queryName = "test"; //$NON-NLS-1$
      final CompoundDataFactory cdf = new CompoundDataFactory();
      cdf.add( new MockDataFactory( queryName ) );

      DataFactoryRegistry.getInstance().register( metadata );

      assertTrue( DataFactoryRegistry.getInstance().isRegistered( MockDataFactory.class.getName() ) );
      assertEquals( 1, cdf.getQueryNames().length );
      assertNotNull( "Could not find DataFactory for query", cdf.getDataFactoryForQuery( queryName ) ); //$NON-NLS-1$
    } finally {
      DataFactoryRegistry.getInstance().unregister( metadata );
    }
  }

  public void testGetDataFactoryForName_non_freeform() {
    final DefaultDataFactoryMetaData metadata = new DefaultDataFactoryMetaData( MockDataFactory.class.getName(), "", //$NON-NLS-1$
        "", //$NON-NLS-1$
        false, false, false, false, false, false, // freeform
        false, MaturityLevel.Limited, new DefaultDataFactoryCore(), -1 );

    try {
      final String queryName = "test"; //$NON-NLS-1$
      final CompoundDataFactory cdf = new CompoundDataFactory();
      cdf.add( new MockDataFactory( queryName ) );
      DataFactoryRegistry.getInstance().register( metadata );

      assertTrue( DataFactoryRegistry.getInstance().isRegistered( MockDataFactory.class.getName() ) );
      assertEquals( 1, cdf.getQueryNames().length );
      assertNotNull( "Could not find DataFactory for query", cdf.getDataFactoryForQuery( queryName ) ); //$NON-NLS-1$
    } finally {
      DataFactoryRegistry.getInstance().unregister( metadata );
    }
  }

  public void testGetQueryMetaData_freeform() throws ReportDataFactoryException {
    final DefaultDataFactoryMetaData metadata = new DefaultDataFactoryMetaData( MockDataFactory.class.getName(), "", //$NON-NLS-1$
        "", //$NON-NLS-1$
        false, false, false, false, false, true, // freeform
        false, MaturityLevel.Limited, new DefaultDataFactoryCore(), -1 );

    try {
      final String queryName = "test"; //$NON-NLS-1$
      final CompoundDataFactory cdf = new CompoundDataFactory();
      MockDataFactory factory = new MockDataFactory( queryName );
      cdf.add( factory );

      DataFactoryRegistry.getInstance().register( metadata );
      assertFalse( factory.isDesignTimeCalled() );

      assertTrue( DataFactoryRegistry.getInstance().isRegistered( MockDataFactory.class.getName() ) );
      TableModel tableModel = cdf.queryDesignTimeStructure( queryName, new ReportParameterValues() );
      assertEquals( 0, tableModel.getRowCount() );
      assertTrue( factory.isDesignTimeCalled() );
    } finally {
      DataFactoryRegistry.getInstance().unregister( metadata );
    }
  }

  public void testGetQueryMetaData_NonFreeForm() throws ReportDataFactoryException {
    final DefaultDataFactoryMetaData metadata = new DefaultDataFactoryMetaData( MockDataFactory.class.getName(), "", //$NON-NLS-1$
        "", //$NON-NLS-1$
        false, false, false, false, false, false, // freeform
        false, MaturityLevel.Limited, new DefaultDataFactoryCore(), -1 );

    try {
      final String queryName = "test"; //$NON-NLS-1$
      final CompoundDataFactory cdf = new CompoundDataFactory();
      MockDataFactory factory = new MockDataFactory( queryName );
      cdf.add( factory );

      DataFactoryRegistry.getInstance().register( metadata );
      assertFalse( factory.isDesignTimeCalled() );

      assertTrue( DataFactoryRegistry.getInstance().isRegistered( MockDataFactory.class.getName() ) );
      TableModel tableModel = cdf.queryDesignTimeStructure( queryName, new ReportParameterValues() );
      assertEquals( 0, tableModel.getRowCount() );
      assertTrue( factory.isDesignTimeCalled() );
    } finally {
      DataFactoryRegistry.getInstance().unregister( metadata );
    }
  }

}
