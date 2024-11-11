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


package org.pentaho.reporting.ui.datasources.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.FastPropertyEditorManager;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;


public class SequencePropertyTableModel extends AbstractTableModel implements PropertyTableModel {
  private static final Log logger = LogFactory.getLog( SequencePropertyTableModel.class );

  private static class Parameter {
    private String name;
    private String displayName;
    private Class type;
    private PropertyEditor editor;

    private Parameter( final String name,
                       final String displayName,
                       final Class type,
                       final PropertyEditor editor ) {
      this.name = name;
      this.displayName = displayName;
      this.type = type;
      this.editor = editor;
    }

    public PropertyEditor getEditor() {
      return editor;
    }

    public Class getType() {
      return type;
    }

    public String toString() {
      return displayName;
    }

    public String getName() {
      return name;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  private Sequence sequence;
  private ArrayList<Parameter> properties;

  public SequencePropertyTableModel() {
    this.properties = new ArrayList<Parameter>();
  }

  public Sequence getSequence() {
    return sequence;
  }

  public void setSequence( final Sequence sequence ) {
    this.properties.clear();
    this.sequence = sequence;
    if ( this.sequence != null ) {
      final SequenceDescription sequenceDescription = this.sequence.getSequenceDescription();
      final int parameterCount = sequenceDescription.getParameterCount();
      for ( int i = 0; i < parameterCount; i++ ) {
        try {
          final Parameter parameter = new Parameter
            ( sequenceDescription.getParameterName( i ),
              sequenceDescription.getParameterDisplayName( i, Locale.getDefault() ),
              sequenceDescription.getParameterType( i ),
              sequenceDescription.getEditor( i ) );
          this.properties.add( parameter );
        } catch ( MissingResourceException mre ) {
          logger.warn( "Unable to process parameter " + i + " for sequence description " + sequenceDescription );
          // ignore 
        }
      }
    }
    fireTableDataChanged();
  }

  public int getRowCount() {
    if ( sequence == null ) {
      return 0;
    }
    return sequence.getSequenceDescription().getParameterCount();
  }

  public int getColumnCount() {
    return 2;
  }

  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return Messages.getString( "SequencePropertyTableModel.Name" );
    }
    return Messages.getString( "SequencePropertyTableModel.Value" );
  }

  public Class getColumnClass( final int columnIndex ) {
    if ( columnIndex == 0 ) {
      return Parameter.class;
    }
    return Object.class;
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return columnIndex == 1;
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    if ( columnIndex != 1 ) {
      return;
    }

    final Parameter parameter = properties.get( rowIndex );
    if ( aValue == null || parameter.getType().isInstance( aValue ) ) {
      sequence.setParameter( parameter.getName(), aValue );
      fireTableCellUpdated( rowIndex, columnIndex );
    }
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final Parameter parameter = properties.get( rowIndex );
    if ( columnIndex == 0 ) {
      return parameter;
    }
    return sequence.getParameter( parameter.getName() );
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    if ( column != 1 ) {
      return null;
    }

    final Parameter parameter = properties.get( row );
    final PropertyEditor editor = parameter.getEditor();
    if ( editor != null ) {
      return editor;
    }

    if ( String.class.equals( parameter.getType() ) ) {
      return null;
    }

    return FastPropertyEditorManager.findEditor( parameter.getType() );
  }

  public Class getClassForCell( final int row, final int column ) {
    if ( column == 0 ) {
      return Parameter.class;
    }
    final Parameter parameter = properties.get( row );
    return parameter.getType();
  }
}
