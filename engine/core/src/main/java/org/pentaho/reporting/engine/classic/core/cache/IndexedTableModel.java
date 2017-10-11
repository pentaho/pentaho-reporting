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

package org.pentaho.reporting.engine.classic.core.cache;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class IndexedTableModel implements CloseableTableModel, MetaTableModel {
  protected static class ColumnIndexDataAttributes implements DataAttributes {
    private DataAttributes backend;
    private Boolean indexColumn;
    private String name;
    private Class type;
    private String label;

    public ColumnIndexDataAttributes( final DataAttributes backend, final Boolean indexColumn, final String name,
        final Class type, final String label ) {
      this.backend = backend;
      this.indexColumn = indexColumn;
      this.name = name;
      this.type = type;
      this.label = label;

      if ( backend == null ) {
        this.backend = EmptyDataAttributes.INSTANCE;
      }
    }

    public String[] getMetaAttributeDomains() {
      final LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
      namespaces.addAll( Arrays.asList( backend.getMetaAttributeDomains() ) );
      namespaces.add( MetaAttributeNames.Core.NAMESPACE );
      namespaces.add( MetaAttributeNames.Formatting.NAMESPACE );
      return namespaces.toArray( new String[namespaces.size()] );
    }

    public String[] getMetaAttributeNames( final String domainName ) {
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domainName ) == false
          && MetaAttributeNames.Formatting.NAMESPACE.equals( domainName ) == false ) {
        return backend.getMetaAttributeNames( domainName );
      }

      final LinkedHashSet<String> names = new LinkedHashSet<String>();
      names.addAll( Arrays.asList( backend.getMetaAttributeNames( domainName ) ) );
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domainName ) ) {
        names.add( MetaAttributeNames.Core.INDEXED_COLUMN );
        names.add( MetaAttributeNames.Core.NAME );
        names.add( MetaAttributeNames.Core.SOURCE );
        names.add( MetaAttributeNames.Core.TYPE );
      }
      if ( MetaAttributeNames.Formatting.NAMESPACE.equals( domainName ) ) {
        names.add( MetaAttributeNames.Formatting.LABEL );
      }
      return names.toArray( new String[names.size()] );
    }

    /**
     * @param domain
     *          never null.
     * @param name
     *          never null.
     * @param type
     *          can be null.
     * @param context
     *          never null.
     * @return
     */
    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context ) {
      return getMetaAttribute( domain, name, type, context, null );
    }

    /**
     * @param domain
     *          never null.
     * @param name
     *          never null.
     * @param type
     *          can be null.
     * @param context
     *          never null.
     * @param defaultValue
     *          can be null
     * @return
     */
    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context, final Object defaultValue ) {
      final Object retval = backend.getMetaAttribute( domain, name, type, context, defaultValue );
      if ( retval != null ) {
        return retval;
      }
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Core.INDEXED_COLUMN.equals( name ) ) {
          return convert( DefaultConceptQueryMapper.INSTANCE, context, indexColumn, type );
        }
        if ( MetaAttributeNames.Core.NAME.equals( name ) ) {
          return convert( DefaultConceptQueryMapper.INSTANCE, context, this.name, type );
        }
        if ( MetaAttributeNames.Core.TYPE.equals( name ) ) {
          return convert( DefaultConceptQueryMapper.INSTANCE, context, this.type, type );
        }
        if ( MetaAttributeNames.Core.SOURCE.equals( name ) ) {
          return convert( DefaultConceptQueryMapper.INSTANCE, context, MetaAttributeNames.Core.SOURCE_VALUE_TABLE, type );
        }
      }
      if ( MetaAttributeNames.Formatting.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Formatting.LABEL.equals( name ) ) {
          return convert( DefaultConceptQueryMapper.INSTANCE, context, this.label, type );
        }
      }
      return defaultValue;
    }

    private Object convert( final ConceptQueryMapper mapper, final DataAttributeContext context, final Object value,
        final Class type ) {
      return mapper.getValue( value, type, context );
    }

    public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Core.INDEXED_COLUMN.equals( name ) ) {
          return DefaultConceptQueryMapper.INSTANCE;
        }
        if ( MetaAttributeNames.Core.NAME.equals( name ) ) {
          return DefaultConceptQueryMapper.INSTANCE;
        }
        if ( MetaAttributeNames.Core.TYPE.equals( name ) ) {
          return DefaultConceptQueryMapper.INSTANCE;
        }
        if ( MetaAttributeNames.Core.SOURCE.equals( name ) ) {
          return DefaultConceptQueryMapper.INSTANCE;
        }
      }
      if ( MetaAttributeNames.Formatting.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Formatting.LABEL.equals( name ) ) {
          return DefaultConceptQueryMapper.INSTANCE;
        }
      }

      final ConceptQueryMapper retval = backend.getMetaAttributeMapper( domain, name );
      if ( retval != null ) {
        return retval;
      }
      return null;
    }

    public Object clone() throws CloneNotSupportedException {
      final ColumnIndexDataAttributes dataAttributes = (ColumnIndexDataAttributes) super.clone();
      dataAttributes.backend = (DataAttributes) backend.clone();
      return dataAttributes;
    }
  }

  private CloseableTableModel closeableTableModel;
  private TableModel backend;

  public IndexedTableModel( final TableModel backend ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    if ( backend instanceof CloseableTableModel ) {
      closeableTableModel = (CloseableTableModel) backend;
    }
    if ( backend instanceof IndexedTableModel ) {
      throw new IllegalStateException();
    }
    this.backend = backend;
  }

  /**
   * If this model has disposeable resources assigned, close them or dispose them.
   */
  public void close() {
    if ( closeableTableModel != null ) {
      closeableTableModel.close();
    }
  }

  public int getRowCount() {
    return backend.getRowCount();
  }

  public int getColumnCount() {
    return 2 * backend.getColumnCount();
  }

  protected int indexToColumn( final int col ) {
    if ( col < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    final int count = backend.getColumnCount();
    if ( col >= ( count * 2 ) ) {
      throw new IndexOutOfBoundsException( "Requested column '" + col + "' is greater than '" + ( count * 2 ) + "'" );
    }
    if ( col < count ) {
      return col;
    }
    return col - count;
  }

  public String getColumnName( final int columnIndex ) {
    if ( columnIndex < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( columnIndex < backend.getColumnCount() ) {
      return backend.getColumnName( columnIndex );
    }

    return ClassicEngineBoot.INDEX_COLUMN_PREFIX + indexToColumn( columnIndex );
  }

  public Class getColumnClass( final int columnIndex ) {
    return backend.getColumnClass( indexToColumn( columnIndex ) );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return backend.isCellEditable( rowIndex, indexToColumn( columnIndex ) );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return backend.getValueAt( rowIndex, indexToColumn( columnIndex ) );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    backend.setValueAt( aValue, rowIndex, indexToColumn( columnIndex ) );
  }

  public void addTableModelListener( final TableModelListener l ) {
    backend.addTableModelListener( l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    backend.removeTableModelListener( l );
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the caller is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Be aware that cell-level attributes do not make it into the designtime dataschema, as this dataschema only looks at
   * the structural metadata available and does not contain any data references.
   *
   * @param row
   *          the row of the cell for which the meta-data is queried.
   * @param column
   *          the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Checks, whether cell-data attributes are supported by this tablemodel implementation.
   *
   * @return true, if the model supports cell-level attributes, false otherwise.
   */
  public boolean isCellDataAttributesSupported() {
    return false;
  }

  /**
   * Returns the column-level attributes for the given column.
   *
   * @param column
   *          the column.
   * @return data-attributes, never null.
   */
  public DataAttributes getColumnAttributes( final int column ) {
    if ( column < backend.getColumnCount() ) {
      return new ColumnIndexDataAttributes( null, Boolean.FALSE, getColumnName( column ), getColumnClass( column ),
          getColumnName( column ) );
    } else {
      return new ColumnIndexDataAttributes( null, Boolean.TRUE, getColumnName( column ), getColumnClass( column ),
          getColumnName( column - backend.getColumnCount() ) );
    }
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table-attributes, never null.
   */
  public DataAttributes getTableAttributes() {
    return EmptyDataAttributes.INSTANCE;
  }

  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append( "IndexedTableModel" );
    sb.append( "={backend=" ).append( backend );
    sb.append( '}' );
    return sb.toString();
  }
}
