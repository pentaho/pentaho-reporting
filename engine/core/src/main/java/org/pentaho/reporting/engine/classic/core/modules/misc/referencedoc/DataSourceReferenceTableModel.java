/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.referencedoc;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A table model for the style key reference generator.
 *
 * @author Thomas Morgner
 */
public class DataSourceReferenceTableModel extends AbstractTableModel {
  /**
   * Represents a row in the table model.
   */
  private static class DataSourceDescriptionRow {
    /**
     * The factory.
     */
    private final DataSourceFactory datasourceFactory;

    /**
     * The key.
     */
    private final String datasourceName;

    /**
     * The implementing class for the datasource name.
     */
    private final Class implementingClass;

    /**
     * Creates a new row.
     *
     * @param datasourceFactory
     *          the datasource factory
     * @param name
     *          the name of the datasource within the factory.
     * @param implementingClass
     *          the class that implements the named datasource.
     */
    private DataSourceDescriptionRow( final DataSourceFactory datasourceFactory, final String name,
        final Class implementingClass ) {
      this.datasourceFactory = datasourceFactory;
      this.datasourceName = name;
      this.implementingClass = implementingClass;
    }

    /**
     * Returns the factory.
     *
     * @return The factory.
     */
    public DataSourceFactory getFactory() {
      return datasourceFactory;
    }

    /**
     * Returns the datasource name.
     *
     * @return The datasource name.
     */
    public String getName() {
      return datasourceName;
    }

    /**
     * Returns the class object for the datasource.
     *
     * @return the datasource class.
     */
    public Class getImplementingClass() {
      return implementingClass;
    }
  }

  /**
   * The column names.
   */
  private static final String[] COLUMN_NAMES = { "datasource-factory", //$NON-NLS-1$
    "datasource-name", //$NON-NLS-1$
    "datasource-class" //$NON-NLS-1$
  };

  /**
   * Storage for the rows.
   */
  private final ArrayList rows;

  /**
   * Creates a new table model.
   *
   * @param cf
   *          the factory collection.
   */
  public DataSourceReferenceTableModel( final DataSourceCollector cf ) {
    rows = new ArrayList();
    addFactoryCollector( cf );
  }

  /**
   * Adds a factory.
   *
   * @param cf
   *          the factory.
   */
  private void addFactoryCollector( final DataSourceCollector cf ) {
    final Iterator it = cf.getFactories();
    while ( it.hasNext() ) {
      final DataSourceFactory cfact = (DataSourceFactory) it.next();
      if ( cfact instanceof DataSourceCollector ) {
        addFactoryCollector( (DataSourceCollector) cfact );
      } else {
        addDataSourceFactory( cfact );
      }
    }
  }

  /**
   * Adds a factory.
   *
   * @param cf
   *          the factory.
   */
  private void addDataSourceFactory( final DataSourceFactory cf ) {
    Iterator it = cf.getRegisteredNames();
    final ArrayList factories = new ArrayList();

    while ( it.hasNext() ) {
      final String c = (String) it.next();
      factories.add( c );
    }

    Collections.sort( factories );
    it = factories.iterator();

    while ( it.hasNext() ) {
      final String keyName = (String) it.next();
      final ObjectDescription od = cf.getDataSourceDescription( keyName );
      rows.add( new DataSourceDescriptionRow( cf, keyName, od.getObjectClass() ) );
    }
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return rows.size();
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return DataSourceReferenceTableModel.COLUMN_NAMES.length;
  }

  /**
   * Returns the column name.
   *
   * @param column
   *          the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    return DataSourceReferenceTableModel.COLUMN_NAMES[column];
  }

  /**
   * Returns <code>String.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex
   *          the column being queried
   * @return the Object.class
   */
  public Class getColumnClass( final int columnIndex ) {
    return String.class;
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex
   *          the row whose value is to be queried
   * @param columnIndex
   *          the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final DataSourceDescriptionRow or = (DataSourceDescriptionRow) rows.get( rowIndex );
    switch ( columnIndex ) {
      case 0:
        return String.valueOf( or.getFactory().getClass().getName() );
      case 1:
        return String.valueOf( or.getName() );
      case 2:
        return String.valueOf( or.getImplementingClass().getName() );
      default:
        return null;
    }
  }
}
