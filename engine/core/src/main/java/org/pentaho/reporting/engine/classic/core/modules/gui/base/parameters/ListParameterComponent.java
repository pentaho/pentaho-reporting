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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.awt.Component;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.MinimalScrollPane;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

//import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class ListParameterComponent extends MinimalScrollPane implements ParameterComponent {
  private class ListUpdateHandler implements ChangeListener {
    private ListUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( adjustingToUserInput ) {
        return;
      }

      try {

        initialize();
      } catch ( ReportDataFactoryException rdfe ) {
        throw new IllegalStateException( "Failed:" + rdfe.getMessage(), rdfe );
      }
    }

  }

  private class SingleValueListParameterHandler implements ListSelectionListener {
    private String key;

    public SingleValueListParameterHandler( final String key ) {
      this.key = key;
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( adjustingToExternalInput || adjustingToUserInput ) {
        return;
      }

      if ( e.getValueIsAdjusting() ) {
        return;
      }
      try {
        adjustingToUserInput = true;

        final KeyedComboBoxModel theModel = (KeyedComboBoxModel) list.getModel();
        final int index = list.getSelectedIndex();
        if ( index == -1 ) {
          updateContext.setParameterValue( key, null );
        } else {
          updateContext.setParameterValue( key, theModel.getKeyAt( index ) );
        }
      } finally {
        adjustingToUserInput = false;
      }
    }
  }

  private class MultiValueListParameterHandler implements ListSelectionListener {
    private String key;

    public MultiValueListParameterHandler( final String key ) {
      this.key = key;
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( adjustingToExternalInput || adjustingToUserInput ) {
        return;
      }
      if ( e.getValueIsAdjusting() ) {
        return;
      }

      try {
        adjustingToUserInput = true;

        final KeyedComboBoxModel listModel = (KeyedComboBoxModel) list.getModel();
        final ListSelectionModel selectionModel = list.getSelectionModel();
        selectionModel.setValueIsAdjusting( true );

        // Determine if this selection has added or removed items
        final HashSet<Integer> newSelections = new HashSet<Integer>();
        final int size = listModel.getSize();
        for ( int i = 0; i < size; i++ ) {
          if ( selectionModel.isSelectedIndex( i ) ) {
            newSelections.add( i );
          }
        }

        // Turn on everything that was selected previously
        Iterator it = selectionCache.iterator();
        while ( it.hasNext() ) {
          final Integer integer = (Integer) it.next();
          final int index = integer.intValue();
          selectionModel.addSelectionInterval( index, index );
        }

        // Add or remove the delta

        if ( newSelections.containsAll( selectionCache ) == false ) {
          it = newSelections.iterator();
          while ( it.hasNext() ) {
            final Integer nextInt = (Integer) it.next();
            final int index = nextInt.intValue();
            if ( selectionCache.contains( nextInt ) ) {
              selectionModel.removeSelectionInterval( index, index );
            } else {
              selectionModel.addSelectionInterval( index, index );
            }
          }

          // Save selections for next time
          selectionCache.clear();
          for ( int i = 0; i < size; i++ ) {
            if ( selectionModel.isSelectedIndex( i ) ) {
              selectionCache.add( i );
            }
          }
        }

        selectionModel.setValueIsAdjusting( false );

        final int[] indices = list.getSelectedIndices();
        final Object[] keys = new Object[indices.length];
        for ( int i = 0; i < keys.length; i++ ) {
          final int index = indices[i];
          keys[i] = listModel.getKeyAt( index );
        }
        updateContext.setParameterValue( key, keys );
      } finally {
        adjustingToUserInput = false;
      }
    }
  }

  private static class FixedTheJDKListCellRenderer extends DefaultListCellRenderer {
    private FixedTheJDKListCellRenderer() {
    }

    public Component getListCellRendererComponent( final JList list, final Object value, final int index,
        final boolean isSelected, final boolean cellHasFocus ) {
      if ( value == null ) {
        return super.getListCellRendererComponent( list, "<null>", index, isSelected, cellHasFocus ); //$NON-NLS-1$
      }
      if ( "".equals( value ) ) {
        return super.getListCellRendererComponent( list, " ", index, isSelected, cellHasFocus );
      }
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }
  }

  private ListParameter listParameter;
  private ParameterContext parameterContext;
  private ParameterUpdateContext updateContext;
  private JList list;
  private boolean adjustingToUserInput;
  private boolean adjustingToExternalInput;
  private ArrayList<Integer> selectionCache;
  private ListUpdateHandler changeListener;

  /**
   * Constructs a <code>JList</code> with an empty model.
   */
  public ListParameterComponent( final ListParameter listParameter, final ParameterUpdateContext updateContext,
      final ParameterContext parameterContext ) {
    this.listParameter = listParameter;
    this.updateContext = updateContext;
    this.parameterContext = parameterContext;
    this.selectionCache = new ArrayList<Integer>();

    list = new JList();
    list.setCellRenderer( new FixedTheJDKListCellRenderer() );

    if ( listParameter.isAllowMultiSelection() ) {
      list.addListSelectionListener( new MultiValueListParameterHandler( listParameter.getName() ) );
      list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
    } else {
      list.addListSelectionListener( new SingleValueListParameterHandler( listParameter.getName() ) );
      list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }

    final String layout =
        listParameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.LAYOUT, parameterContext );
    if ( "horizontal".equals( layout ) ) { //$NON-NLS-1$
      list.setLayoutOrientation( JList.HORIZONTAL_WRAP );
      list.setVisibleRowCount( 1 );
      list.setPreferredSize( new Dimension( (int) list.getMinimumSize().getWidth(), 25 ) );
    } else {
      final String visibleItemsText =
          listParameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.VISIBLE_ITEMS, parameterContext );
      final int visibleItems = ParserUtil.parseInt( visibleItemsText, 0 );
      if ( visibleItems > 0 ) {
        list.setVisibleRowCount( visibleItems );
      }
    }

    setViewportView( list );
    getViewport().setMinimumSize( list.getPreferredScrollableViewportSize() );
    setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );

    changeListener = new ListUpdateHandler();
    updateContext.addChangeListener( changeListener );
  }

  public JComponent getUIComponent() {
    return this;
  }

  public void initialize() throws ReportDataFactoryException {
    adjustingToExternalInput = true;
    try {
      final KeyedComboBoxModel keyedComboBoxModel =
          DefaultParameterComponentFactory.createModel( listParameter, parameterContext );
      list.setModel( keyedComboBoxModel );

      final ListSelectionModel selectionModel = list.getSelectionModel();
      final Object value = updateContext.getParameterValue( listParameter.getName() );
      final HashSet keylist = getNormalizedSet( value );
      selectionModel.setValueIsAdjusting( true );
      list.clearSelection();

      final int size = keyedComboBoxModel.getSize();
      for ( int i = 0; i < size; i++ ) {
        final Object key = keyedComboBoxModel.getKeyAt( i );
        if ( isSafeMatch( key, keylist ) ) {
          selectionModel.addSelectionInterval( i, i );
        }
      }
      selectionModel.setValueIsAdjusting( false );
    } finally {
      adjustingToExternalInput = false;
    }
  }

  private boolean isSafeMatch( final Object key, final Collection values ) {
    for ( final Object value : values ) {
      if ( key == value ) {
        return true;
      }
      if ( key != null && key.equals( value ) ) {
        return true;
      }
      if ( key instanceof Number && value instanceof Number ) {
        final BigDecimal bdK = new BigDecimal( key.toString() );
        final BigDecimal bdV = new BigDecimal( value.toString() );
        if ( bdK.compareTo( bdV ) == 0 ) {
          return true;
        }
      }
      if ( key instanceof Date && value instanceof Date ) {
        final Date d1 = (Date) key;
        final Date d2 = (Date) value;
        if ( d1.getTime() == d2.getTime() ) {
          return true;
        }
      }
    }
    return false;
  }

  protected JList getList() {
    return list;
  }

  /**
   * Creates a selection set out of the value given. If the value is null, a set with a null-member is returned.
   *
   * @param o
   *          the value for which a selection set should be returned.
   * @return the selection set.
   * @noinspection unchecked
   */
  private HashSet getNormalizedSet( final Object o ) {
    final HashSet set = new HashSet();
    if ( o instanceof Object[] ) {
      final Object[] oa = (Object[]) o;
      set.addAll( Arrays.asList( oa ) );
    } else if ( o instanceof Collection ) {
      set.addAll( (Collection) o );
    } else {
      set.add( o );
    }
    return set;
  }
}
