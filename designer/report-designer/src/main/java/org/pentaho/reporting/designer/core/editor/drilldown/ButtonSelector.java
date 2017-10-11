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

package org.pentaho.reporting.designer.core.editor.drilldown;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 14:06:42
 *
 * @author Thomas Morgner.
 */
public class ButtonSelector extends JPanel implements DrillDownSelector {
  private class SelectorAction implements ActionListener {
    private DrillDownUiProfile profile;

    private SelectorAction( final DrillDownUiProfile profile ) {
      this.profile = profile;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      setSelectedProfile( profile );
    }
  }

  private EventListenerList eventListeners;
  private DrillDownUiProfile selectedItem;
  private HashMap<DrillDownUiProfile, JRadioButton> profilesToButton;

  public ButtonSelector() {
    eventListeners = new EventListenerList();
    profilesToButton = new HashMap<DrillDownUiProfile, JRadioButton>();

    final ButtonGroup buttonGroup = new ButtonGroup();
    final DrillDownUiProfileRegistry metaData = DrillDownUiProfileRegistry.getInstance();
    final DrillDownUiProfile[] drilldownProfiles = metaData.getProfiles();
    Arrays.sort( drilldownProfiles, new DrillDownUiProfileComparator() );
    for ( int i = 0; i < drilldownProfiles.length; i++ ) {
      final DrillDownUiProfile profile = drilldownProfiles[ i ];
      final JRadioButton button = new JRadioButton( profile.getDisplayName() );
      button.addActionListener( new SelectorAction( profile ) );
      add( button );
      buttonGroup.add( button );
      profilesToButton.put( profile, button );
    }

    final JRadioButton button = new JRadioButton( "Manual Linking" );
    button.addActionListener( new SelectorAction( null ) );
    add( button );
    buttonGroup.add( button );
    profilesToButton.put( null, button );

  }

  public DrillDownUiProfile getSelectedProfile() {
    return selectedItem;
  }

  public void setSelectedProfile( final DrillDownUiProfile profile ) {
    selectedItem = profile;

    final JRadioButton button = profilesToButton.get( selectedItem );
    if ( button != null ) {
      button.setSelected( true );
    }
    fireChangeEvent();
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

  public JComponent getComponent() {
    return this;
  }
}
