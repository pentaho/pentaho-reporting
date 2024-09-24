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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

import javax.swing.table.TableModel;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

/**
 * A utility class that prints out information about a TableModel.
 *
 * @author Thomas Morgner
 */
public final class TableModelInfo {
  /**
   * DefaultConstructor.
   */
  private TableModelInfo() {
  }

  public static void printTableMetaData( final TableModel mod, final PrintStream out ) {
    if ( mod instanceof MetaTableModel == false ) {
      out.println( "TableModel has no meta-data." );
      return;
    }

    final MetaTableModel metaTableModel = (MetaTableModel) mod;
    final DataAttributes tableAttributes = metaTableModel.getTableAttributes();
    final DataAttributeContext attributeContext =
        new DefaultDataAttributeContext( new GenericOutputProcessorMetaData(), Locale.US );

    final String[] tableAttrDomains = tableAttributes.getMetaAttributeDomains();
    Arrays.sort( tableAttrDomains );
    for ( int i = 0; i < tableAttrDomains.length; i++ ) {
      final String tableAttrDomain = tableAttrDomains[i];
      final String[] attributeNames = tableAttributes.getMetaAttributeNames( tableAttrDomain );
      Arrays.sort( attributeNames );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String attributeName = attributeNames[j];
        final Object o =
            tableAttributes.getMetaAttribute( tableAttrDomain, attributeName, Object.class, attributeContext );

        out.println( "TableAttribute [" + tableAttrDomain + ':' + attributeName + "]=" + format( o ) );
      }
    }

    for ( int column = 0; column < mod.getColumnCount(); column++ ) {
      final DataAttributes columnAttributes = metaTableModel.getColumnAttributes( column );
      final String[] columnAttributeDomains = columnAttributes.getMetaAttributeDomains();
      Arrays.sort( columnAttributeDomains );
      for ( int i = 0; i < columnAttributeDomains.length; i++ ) {
        final String colAttrDomain = columnAttributeDomains[i];
        final String[] attributeNames = columnAttributes.getMetaAttributeNames( colAttrDomain );
        Arrays.sort( attributeNames );
        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[j];
          final Object o =
              columnAttributes.getMetaAttribute( colAttrDomain, attributeName, Object.class, attributeContext );

          out.println( "ColumnAttribute(" + column + ") [" + colAttrDomain + ':' + attributeName + "]=" + format( o ) );
        }
      }
    }
  }

  public static void printTableCellAttributes( final TableModel mod, final PrintStream out ) {
    if ( mod instanceof MetaTableModel == false ) {
      out.println( "TableModel has no meta-data." );
      return;
    }

    final MetaTableModel metaTableModel = (MetaTableModel) mod;
    if ( metaTableModel.isCellDataAttributesSupported() == false ) {
      out.println( "TableModel has no cell-meta-data." );
      return;
    }

    final DataAttributeContext attributeContext =
        new DefaultDataAttributeContext( new GenericOutputProcessorMetaData(), Locale.US );

    out.println( "Tablemodel contains " + mod.getRowCount() + " rows." ); //$NON-NLS-1$ //$NON-NLS-2$
    out.println( "Checking the attributes inside" ); //$NON-NLS-1$
    for ( int rows = 0; rows < mod.getRowCount(); rows++ ) {
      for ( int i = 0; i < mod.getColumnCount(); i++ ) {
        final DataAttributes cellAttributes = metaTableModel.getCellDataAttributes( rows, i );
        final String[] columnAttributeDomains = cellAttributes.getMetaAttributeDomains();
        Arrays.sort( columnAttributeDomains );
        for ( int attrDomainIdx = 0; attrDomainIdx < columnAttributeDomains.length; attrDomainIdx++ ) {
          final String colAttrDomain = columnAttributeDomains[attrDomainIdx];
          final String[] attributeNames = cellAttributes.getMetaAttributeNames( colAttrDomain );
          Arrays.sort( attributeNames );
          for ( int j = 0; j < attributeNames.length; j++ ) {
            final String attributeName = attributeNames[j];
            final Object o =
                cellAttributes.getMetaAttribute( colAttrDomain, attributeName, Object.class, attributeContext );

            out.println( "CellAttribute(" + rows + ", " + i + ") [" + colAttrDomain + ':' + attributeName + "]="
                + format( o ) );
          }
        }
      }
    }
  }

  public static void printTableModel( final TableModel mod, final PrintStream out ) {
    out.println( "Tablemodel contains " + mod.getRowCount() + " rows." ); //$NON-NLS-1$ //$NON-NLS-2$
    for ( int i = 0; i < mod.getColumnCount(); i++ ) {
      out.println( "Column: " + i + " Name=" + mod.getColumnName( i ) + "; DataType=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + mod.getColumnClass( i ) );
    }

    out.println( "Checking the data inside" ); //$NON-NLS-1$
    for ( int rows = 0; rows < mod.getRowCount(); rows++ ) {
      for ( int i = 0; i < mod.getColumnCount(); i++ ) {
        final Object value = mod.getValueAt( rows, i );
        final Class<?> c = mod.getColumnClass( i );
        if ( value == null ) {
          out.println( "ValueAt (" + rows + ", " + i + ") is null" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
          if ( c.isAssignableFrom( value.getClass() ) == false ) {
            out.println( "ValueAt (" + rows + ", " + i + ") is not assignable from " + c ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          } else if ( c.equals( Object.class ) ) {
            out.println( "ValueAt (" + rows + ", " + i + ") is in a generic column and is of "
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "type " + value.getClass() ); //$NON-NLS-1$
          } else {
            out.println( "ValueAt (" + rows + ", " + i + ") is in a typed column and is of "
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "type " + value.getClass() ); //$NON-NLS-1$
          }
        }
      }
    }
  }

  /**
   * Prints a table model to standard output.
   *
   * @param mod
   *          the model.
   */
  public static void printTableModel( final TableModel mod ) {
    printTableModel( mod, System.out );
  }

  public static void printTableModelContents( final TableModel mod, final PrintStream out ) {
    out.println( "Tablemodel contains " + mod.getRowCount() + " rows." ); //$NON-NLS-1$ //$NON-NLS-2$
    for ( int i = 0; i < mod.getColumnCount(); i++ ) {
      out.println( "Column: " + i + " Name=" + mod.getColumnName( i ) + "; DataType=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + mod.getColumnClass( i ) );
    }

    out.println( "Checking the data inside" ); //$NON-NLS-1$
    for ( int rows = 0; rows < mod.getRowCount(); rows++ ) {
      for ( int i = 0; i < mod.getColumnCount(); i++ ) {
        final Object value = mod.getValueAt( rows, i );
        // final Class c = mod.getColumnClass(i);
        out.println( "ValueAt (" + rows + ", " + i + ") is '" + format( value ) + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
    }
  }

  private static String format( final Object value ) {
    if ( value instanceof Float || value instanceof Double ) {
      final DecimalFormat fmt = new DecimalFormat( "#0.000", new DecimalFormatSymbols( Locale.US ) );
      return fmt.format( value );
    }
    return String.valueOf( value );
  }

  /**
   * Prints a table model to standard output.
   *
   * @param mod
   *          the model.
   */
  public static void printTableModelContents( final TableModel mod ) {
    printTableModelContents( mod, System.out );
  }
}
