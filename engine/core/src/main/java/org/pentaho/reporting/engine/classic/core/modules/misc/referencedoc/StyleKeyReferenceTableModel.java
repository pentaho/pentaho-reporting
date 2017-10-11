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

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactoryCollector;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A table model for the style key reference generator.
 *
 * @author Thomas Morgner
 */
public class StyleKeyReferenceTableModel extends AbstractTableModel {
  /**
   * Represents a row in the table model.
   */
  private static class StylekeyDescriptionRow {
    /**
     * The factory.
     */
    private final StyleKeyFactory keyFactory;

    /**
     * The key.
     */
    private final StyleKey key;

    /**
     * Creates a new row.
     *
     * @param keyFactory
     *          the factory.
     * @param key
     *          the key.
     */
    private StylekeyDescriptionRow( final StyleKeyFactory keyFactory, final StyleKey key ) {
      this.keyFactory = keyFactory;
      this.key = key;
    }

    /**
     * Returns the factory.
     *
     * @return The factory.
     */
    public StyleKeyFactory getKeyFactory() {
      return keyFactory;
    }

    /**
     * Returns the key.
     *
     * @return The key.
     */
    public StyleKey getKey() {
      return key;
    }
  }

  /**
   * The column names.
   */
  private static final String[] COLUMN_NAMES = { "stylekey-factory", //$NON-NLS-1$
    "key-name", //$NON-NLS-1$
    "key-class", //$NON-NLS-1$
    "inherit", //$NON-NLS-1$
    "transient" //$NON-NLS-1$
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
  public StyleKeyReferenceTableModel( final StyleKeyFactoryCollector cf ) {
    rows = new ArrayList();
    addStyleKeyFactoryCollector( cf );
  }

  /**
   * Adds a factory.
   *
   * @param cf
   *          the factory.
   */
  private void addStyleKeyFactoryCollector( final StyleKeyFactoryCollector cf ) {
    final Iterator it = cf.getFactories();
    while ( it.hasNext() ) {
      final StyleKeyFactory cfact = (StyleKeyFactory) it.next();
      if ( cfact instanceof StyleKeyFactoryCollector ) {
        addStyleKeyFactoryCollector( (StyleKeyFactoryCollector) cfact );
      } else {
        addStyleKeyFactory( cfact );
      }
    }
  }

  /**
   * Adds a factory.
   *
   * @param cf
   *          the factory.
   */
  private void addStyleKeyFactory( final StyleKeyFactory cf ) {
    Iterator it = cf.getRegisteredKeys();
    final ArrayList factories = new ArrayList();

    while ( it.hasNext() ) {
      final String c = (String) it.next();
      factories.add( c );
    }

    Collections.sort( factories );
    it = factories.iterator();

    while ( it.hasNext() ) {
      final String keyName = (String) it.next();
      final StyleKey key = cf.getStyleKey( keyName );
      rows.add( new StylekeyDescriptionRow( cf, key ) );
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
    return StyleKeyReferenceTableModel.COLUMN_NAMES.length;
  }

  /**
   * Returns the column name.
   *
   * @param column
   *          the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    return StyleKeyReferenceTableModel.COLUMN_NAMES[column];
  }

  /**
   * Returns <code>String.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex
   *          the column being queried
   * @return the Object.class
   */
  public Class getColumnClass( final int columnIndex ) {
    if ( columnIndex < 3 ) {
      return String.class;
    }

    return Boolean.class;
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
    final StylekeyDescriptionRow or = (StylekeyDescriptionRow) rows.get( rowIndex );
    switch ( columnIndex ) {
      case 0:
        return String.valueOf( or.getKeyFactory().getClass().getName() );
      case 1:
        return String.valueOf( or.getKey().getName() );
      case 2:
        return String.valueOf( or.getKey().getValueType().getName() );
      case 3:
        return ( or.getKey().isInheritable() ) ? Boolean.TRUE : Boolean.FALSE;
      case 4:
        return ( or.getKey().isTransient() ) ? Boolean.TRUE : Boolean.FALSE;
      default:
        return null;
    }
  }
}
