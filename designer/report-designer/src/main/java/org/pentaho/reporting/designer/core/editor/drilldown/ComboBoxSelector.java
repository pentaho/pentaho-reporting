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


package org.pentaho.reporting.designer.core.editor.drilldown;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 13:50:29
 *
 * @author Thomas Morgner.
 */
public class ComboBoxSelector extends JComboBox implements DrillDownSelector {
  private static class DrillDownProfileRenderer extends DefaultListCellRenderer {
    private DrillDownProfileRenderer() {
    }

    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
      if ( value == null ) {
        setText( "Manual Linking" );
      } else {
        final DrillDownUiProfile profile = (DrillDownUiProfile) value;
        setText( profile.getDisplayName() );
      }
      return this;
    }
  }

  private class DrillDownItemListener implements ItemListener {
    private DrillDownItemListener() {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged( final ItemEvent e ) {
      fireChangeEvent();
    }

  }

  private EventListenerList eventListeners;

  public ComboBoxSelector( final boolean allowManualMode ) {
    eventListeners = new EventListenerList();
    setRenderer( new DrillDownProfileRenderer() );
    setModel( createModel( allowManualMode ) );
    addItemListener( new DrillDownItemListener() );
  }

  public DrillDownUiProfile getSelectedProfile() {
    return (DrillDownUiProfile) getSelectedItem();
  }

  public void setSelectedProfile( final DrillDownUiProfile profile ) {
    setSelectedItem( profile );
  }

  public JComponent getComponent() {
    return this;
  }

  private DefaultComboBoxModel createModel( final boolean allowManualMode ) {
    final DrillDownUiProfileRegistry metaData = DrillDownUiProfileRegistry.getInstance();
    final DrillDownUiProfile[] drilldownProfiles = metaData.getProfiles();
    Arrays.sort( drilldownProfiles, new DrillDownUiProfileComparator() );

    final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel( drilldownProfiles );
    if ( allowManualMode ) {
      comboBoxModel.addElement( null );
    }
    return comboBoxModel;
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    if ( changeListener == null ) {
      throw new NullPointerException();
    }
    eventListeners.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    if ( changeListener == null ) {
      throw new NullPointerException();
    }
    eventListeners.remove( ChangeListener.class, changeListener );
  }

  private void fireChangeEvent() {
    final ChangeListener[] changeListeners = eventListeners.getListeners( ChangeListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener listener = changeListeners[ i ];
      listener.stateChanged( event );
    }
  }

}
