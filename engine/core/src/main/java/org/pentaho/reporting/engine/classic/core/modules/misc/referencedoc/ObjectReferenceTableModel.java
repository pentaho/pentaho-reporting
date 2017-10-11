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

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;

import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A table model for the objects referenced by the class factories.
 *
 * @author Thomas Morgner.
 */
public class ObjectReferenceTableModel extends AbstractTableModel {
  /**
   * Used to represent each row in the table model.
   */
  private static class ObjectDescriptionRow {
    /**
     * The class factory.
     */
    private final ClassFactory classFactory;

    /**
     * The object class.
     */
    private final Class object;

    /**
     * The parameter name.
     */
    private final String paramName;

    /**
     * The parameter type.
     */
    private final Class paramType;

    /**
     * Creates a new row.
     *
     * @param classFactory
     *          the class factory.
     * @param object
     *          the object class.
     * @param paramName
     *          the parameter name.
     * @param paramType
     *          the parameter type.
     */
    private ObjectDescriptionRow( final ClassFactory classFactory, final Class object, final String paramName,
        final Class paramType ) {
      this.classFactory = classFactory;
      this.object = object;
      this.paramName = paramName;
      this.paramType = paramType;
    }

    /**
     * Returns the class factory.
     *
     * @return The class factory.
     */
    public ClassFactory getClassFactory() {
      return classFactory;
    }

    /**
     * Returns the object class.
     *
     * @return The class.
     */
    public Class getObject() {
      return object;
    }

    /**
     * Returns the parameter name.
     *
     * @return the parameter name.
     */
    public String getParamName() {
      return paramName;
    }

    /**
     * Returns the parameter type.
     *
     * @return the parameter type.
     */
    public Class getParamType() {
      return paramType;
    }
  }

  /**
   * A class name comparator.
   */
  private static class ClassNameComparator implements Comparator, Serializable {
    /**
     * Default-Constructor.
     */
    private ClassNameComparator() {
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     * <p>
     *
     * @param o1
     *          the first object to be compared.
     * @param o2
     *          the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     * @throws ClassCastException
     *           if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare( final Object o1, final Object o2 ) {
      final Class c1 = (Class) o1;
      final Class c2 = (Class) o2;
      return c1.getName().compareTo( c2.getName() );
    }
  }

  /**
   * The table model column names.
   */
  private static final String[] COLUMN_NAMES = { "object-factory", //$NON-NLS-1$
    "object-class", //$NON-NLS-1$
    "parameter-name", //$NON-NLS-1$
    "parameter-class" //$NON-NLS-1$
  };

  /**
   * Storage for the rows.
   */
  private final ArrayList rows;

  /**
   * Creates a new table model for a set of class factories.
   *
   * @param cf
   *          the class factories.
   */
  public ObjectReferenceTableModel( final ClassFactoryCollector cf ) {
    rows = new ArrayList();
    addClassFactoryCollector( cf );
  }

  /**
   * Adds a class factory collector.
   *
   * @param cf
   *          the class factory collector.
   */
  private void addClassFactoryCollector( final ClassFactoryCollector cf ) {
    final Iterator it = cf.getFactories();
    while ( it.hasNext() ) {
      final ClassFactory cfact = (ClassFactory) it.next();
      if ( cfact instanceof ClassFactoryCollector ) {
        addClassFactoryCollector( (ClassFactoryCollector) cfact );
      } else {
        addClassFactory( cfact );
      }
    }
  }

  /**
   * Adds a class factory.
   *
   * @param cf
   *          the class factory.
   */
  private void addClassFactory( final ClassFactory cf ) {
    Iterator it = cf.getRegisteredClasses();
    final ArrayList factories = new ArrayList();

    while ( it.hasNext() ) {
      final Class c = (Class) it.next();
      factories.add( c );
    }

    Collections.sort( factories, new ClassNameComparator() );
    it = factories.iterator();

    while ( it.hasNext() ) {
      final Class c = (Class) it.next();
      final ObjectDescription od = cf.getDescriptionForClass( c );
      Iterator itNames = od.getParameterNames();
      final ArrayList nameList = new ArrayList();
      while ( itNames.hasNext() ) {
        nameList.add( itNames.next() );
      }
      // sort the parameter names
      Collections.sort( nameList );
      itNames = nameList.iterator();
      while ( itNames.hasNext() ) {
        final String name = (String) itNames.next();
        rows.add( new ObjectDescriptionRow( cf, c, name, od.getParameterDefinition( name ) ) );
      }
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
    return ObjectReferenceTableModel.COLUMN_NAMES.length;
  }

  /**
   * Returns the column name.
   *
   * @param column
   *          the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    return ObjectReferenceTableModel.COLUMN_NAMES[column];
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
    final ObjectDescriptionRow or = (ObjectDescriptionRow) rows.get( rowIndex );
    switch ( columnIndex ) {
      case 0:
        return String.valueOf( or.getClassFactory().getClass().getName() );
      case 1:
        return String.valueOf( or.getObject().getName() );
      case 2:
        return String.valueOf( or.getParamName() );
      case 3:
        return String.valueOf( or.getParamType().getName() );
      default:
        return null;
    }
  }

}
