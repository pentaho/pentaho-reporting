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

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.ImmutableDataAttributes;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class CachableTableModel extends AbstractTableModel implements MetaTableModel {

  private GenericObjectTable<DefaultDataAttributes> cellAttributes;
  private GenericObjectTable<Object> cellValues;
  private ArrayList<DataAttributes> columnAttributes;
  private DataAttributes tableAttributes;
  private DataAttributeContext dataAttributeContext;

  public CachableTableModel( final TableModel model ) {
    dataAttributeContext = new DefaultDataAttributeContext();
    columnAttributes = new ArrayList<DataAttributes>();
    if ( model instanceof MetaTableModel ) {
      final MetaTableModel metaTableModel = (MetaTableModel) model;
      initFromMetaModel( metaTableModel );
    } else {
      initDefaultMetaData( model );
    }

    initData( model );

  }

  protected void initFromMetaModel( final MetaTableModel metaTableModel ) {
    final int columnCount = metaTableModel.getColumnCount();
    for ( int i = 0; i < columnCount; i++ ) {
      final DataAttributes originalColAttrs = metaTableModel.getColumnAttributes( i );

      if ( isSaneMetaData( metaTableModel, i ) ) {
        columnAttributes.add( ImmutableDataAttributes.create( originalColAttrs, dataAttributeContext ) );
      } else {
        final String columnName = metaTableModel.getColumnName( i );
        final Class columnType = metaTableModel.getColumnClass( i );
        final DefaultDataAttributes attributes = new DefaultDataAttributes();
        attributes.setMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME,
          DefaultConceptQueryMapper.INSTANCE, columnName );
        attributes.setMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
          DefaultConceptQueryMapper.INSTANCE, columnType );
        attributes.merge( originalColAttrs, dataAttributeContext );
        columnAttributes.add( ImmutableDataAttributes.create( attributes, dataAttributeContext ) );
      }
    }

    if ( metaTableModel.isCellDataAttributesSupported() ) {
      cellAttributes = new GenericObjectTable<DefaultDataAttributes>( metaTableModel.getRowCount(), columnCount );
      for ( int row = 0; row < metaTableModel.getRowCount(); row += 1 ) {
        for ( int columns = 0; columns < columnCount; columns += 1 ) {
          final DefaultDataAttributes attributes = new DefaultDataAttributes();
          attributes.merge( metaTableModel.getCellDataAttributes( row, columns ), dataAttributeContext );
          cellAttributes.setObject( row, columns, attributes );
        }
      }
    }

    final DefaultDataAttributes attributes = new DefaultDataAttributes();
    attributes.merge( metaTableModel.getTableAttributes(), dataAttributeContext );
    tableAttributes = attributes;
  }

  private boolean isSaneMetaData( final MetaTableModel metaTableModel, int i ) {
    final String columnName = metaTableModel.getColumnName( i );
    final Class columnType = metaTableModel.getColumnClass( i );
    final DataAttributes originalColAttrs = metaTableModel.getColumnAttributes( i );

    final String nameFromMeta =
      (String) originalColAttrs.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME,
        String.class, dataAttributeContext );
    if ( !ObjectUtilities.equal( columnName, nameFromMeta ) ) {
      return false;
    }

    final Class colTypeFromMeta =
      (Class<?>) originalColAttrs.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
        Class.class, dataAttributeContext );

    if ( !ObjectUtilities.equal( columnType, colTypeFromMeta ) ) {
      return false;
    }
    return true;
  }

  protected void initData( final TableModel model ) {
    cellValues =
      new GenericObjectTable<Object>( Math.max( 1, model.getRowCount() ), Math.max( 1, model.getColumnCount() ) );
    for ( int row = 0; row < model.getRowCount(); row += 1 ) {
      for ( int columns = 0; columns < model.getColumnCount(); columns += 1 ) {
        cellValues.setObject( row, columns, model.getValueAt( row, columns ) );
      }
    }
  }

  protected void initDefaultMetaData( final TableModel model ) {
    for ( int i = 0; i < model.getColumnCount(); i++ ) {
      final String columnName = model.getColumnName( i );
      final Class columnType = model.getColumnClass( i );
      final DefaultDataAttributes attributes = new DefaultDataAttributes();
      attributes.setMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME,
                                   DefaultConceptQueryMapper.INSTANCE, columnName );
      attributes.setMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
                                   DefaultConceptQueryMapper.INSTANCE, columnType );
      columnAttributes.add( attributes );
    }
    tableAttributes = EmptyDataAttributes.INSTANCE;
  }

  public String getColumnName( final int column ) {
    final DataAttributes attributes = columnAttributes.get( column );
    return (String) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE,
                                                 MetaAttributeNames.Core.NAME, String.class, dataAttributeContext );
  }

  public Class getColumnClass( final int columnIndex ) {
    final DataAttributes attributes = columnAttributes.get( columnIndex );
    return (Class) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE,
                                                MetaAttributeNames.Core.TYPE, Class.class, dataAttributeContext );
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    if ( cellAttributes == null ) {
      throw new IllegalStateException();
    }

    return cellAttributes.getObject( row, column );
  }

  public boolean isCellDataAttributesSupported() {
    return cellAttributes != null;
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return columnAttributes.get( column );
  }

  public DataAttributes getTableAttributes() {
    return tableAttributes;
  }

  public int getRowCount() {
    return cellValues.getRowCount();
  }

  public int getColumnCount() {
    return columnAttributes.size();
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return cellValues.getObject( rowIndex, columnIndex );
  }

  public static boolean isSafeToCache( final TableModel model ) {
    final int columnCount = model.getColumnCount();
    for ( int i = 0; i < columnCount; i += 1 ) {
      Class columnClass = model.getColumnClass( i );
      while ( columnClass.isArray() ) {
        columnClass = columnClass.getComponentType();
      }
      if ( String.class.equals( columnClass ) ) {
        continue;
      }
      if ( Number.class.isAssignableFrom( columnClass ) ) {
        continue;
      }
      if ( Date.class.isAssignableFrom( columnClass ) ) {
        continue;
      }
      if ( Boolean.class.equals( columnClass ) ) {
        continue;
      }
      if ( columnClass.isPrimitive() ) {
        continue;
      }
      if ( Paint.class.isAssignableFrom( columnClass ) ) {
        continue;
      }
      if ( Shape.class.isAssignableFrom( columnClass ) ) {
        continue;
      }
      if ( Stroke.class.isAssignableFrom( columnClass ) ) {
        continue;
      }
      if ( columnClass.isEnum() ) {
        continue;
      }
      return false;
    }
    return true;
  }
}
