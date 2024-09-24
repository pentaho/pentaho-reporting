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

package org.pentaho.reporting.designer.core.settings.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: Martin Date: 12.03.2005 Time: 13:57:59
 */
public class ButtonTabbedPane extends JPanel {
  private static class ScrollablePanel extends JPanel implements Scrollable {
    private ScrollablePanel( final LayoutManager layout ) {
      super( layout );
    }


    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }


    public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 20;
    }


    public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 20;
    }


    public boolean getScrollableTracksViewportWidth() {
      return true;
    }


    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  private class SelectPanelAction implements ActionListener {
    private final String magicKey;

    public SelectPanelAction( final String magicKey ) {
      this.magicKey = magicKey;
    }

    public void actionPerformed( final ActionEvent e ) {
      cardLayout.show( cardPanel, magicKey );
    }
  }

  private ArrayList<Component> settingPlugins;
  private ArrayList<JToggleButton> settingsButtons;
  private JPanel buttonPanel;
  private JPanel cardPanel;
  private CardLayout cardLayout;
  private ButtonGroup panelButtons;

  public ButtonTabbedPane() {
    settingPlugins = new ArrayList<Component>();
    settingsButtons = new ArrayList<JToggleButton>();

    setLayout( new BorderLayout( 0, 0 ) );

    buttonPanel = new JPanel( new GridLayout( 0, 1 ) );

    panelButtons = new ButtonGroup();
    cardLayout = new CardLayout();
    cardPanel = new JPanel( cardLayout );

    final JPanel buttonHelperPanel = new ScrollablePanel( new BorderLayout() );
    buttonHelperPanel.setOpaque( true );
    buttonHelperPanel.add( buttonPanel, BorderLayout.NORTH );

    final JScrollPane scrollPane = new JScrollPane( buttonHelperPanel );
    scrollPane.setOpaque( false );
    scrollPane.setBorder( null );
    scrollPane.getViewport().setBorder( null );
    scrollPane.getViewport().setOpaque( false );

    final JPanel buttonArea = new JPanel( new BorderLayout() );
    buttonArea.add( scrollPane, BorderLayout.CENTER );

    final JPanel helperPanel = new JPanel( new BorderLayout() );
    helperPanel.add( buttonArea, BorderLayout.WEST );
    helperPanel.add( Box.createHorizontalStrut( 5 ), BorderLayout.CENTER );
    add( helperPanel, BorderLayout.WEST );
    add( cardPanel, BorderLayout.CENTER );
  }

  public void showFirst() {
    if ( settingsButtons.isEmpty() == false ) {
      settingsButtons.get( 0 ).setSelected( true );
      cardLayout.first( cardPanel );
    }
  }

  public void addTab( final Icon icon, final String title, final JComponent component ) {
    final String magicKey = String.valueOf( settingPlugins.size() );
    settingPlugins.add( component );
    cardPanel.add( component, magicKey );

    final JToggleButton toggleButton = new JToggleButton( title, icon );
    toggleButton.setHorizontalTextPosition( JToggleButton.CENTER );
    toggleButton.setVerticalTextPosition( JToggleButton.BOTTOM );
    toggleButton.addActionListener( new SelectPanelAction( magicKey ) );

    panelButtons.add( toggleButton );
    settingsButtons.add( toggleButton );

    final JPanel helperPanel = new JPanel( new BorderLayout() );
    helperPanel.setOpaque( false );
    helperPanel.setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 4 ) );
    helperPanel.add( toggleButton, BorderLayout.CENTER );
    buttonPanel.add( helperPanel );
  }

  public int getCardCount() {
    return settingPlugins.size();
  }
}
