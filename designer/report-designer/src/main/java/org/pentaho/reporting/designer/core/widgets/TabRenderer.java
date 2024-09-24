/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.widgets;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.report.CloseReportAction;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabRenderer extends JComponent implements ActionListener {
  private String rawTabName;
  private JLabel label;
  private JButton closeButton;
  private ReportDesignerContext context;
  private JTabbedPane tabbedPane;

  public TabRenderer( final Icon icon, final String tabName,
                      final ReportDesignerContext context, final JTabbedPane tabbedPane ) {
    if ( tabName == null ) {
      throw new NullPointerException();
    }
    this.tabbedPane = tabbedPane;
    this.rawTabName = tabName;
    this.context = context;

    closeButton = new BorderlessButton();
    closeButton.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    closeButton.setPressedIcon( new CloseTabIcon( false, true ) );
    closeButton.setIcon( new CloseTabIcon( false, false ) );
    closeButton.setRolloverIcon( new CloseTabIcon( true, false ) );
    closeButton.setRolloverEnabled( true );
    closeButton.setContentAreaFilled( false );
    closeButton.setBorderPainted( false );
    closeButton.addActionListener( this );

    label = new JLabel( tabName, icon, SwingConstants.LEFT );

    setLayout( new BorderLayout() );
    add( closeButton, BorderLayout.EAST );
    add( label, BorderLayout.CENTER );
  }

  private JTabbedPane getReportEditorPane() {
    return tabbedPane;
  }

  private ReportDesignerContext getContext() {
    return context;
  }

  private int findTab() {
    final JTabbedPane tabbedPane = getReportEditorPane();
    final int count = tabbedPane.getTabCount();
    for ( int i = 0; i < count; i++ ) {
      final Component at = tabbedPane.getTabComponentAt( i );
      if ( at == this ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final int tab = findTab();
    if ( tab == -1 ) {
      return;
    }

    final CloseReportAction cra = new CloseReportAction( tab );
    cra.setReportDesignerContext( getContext() );
    cra.actionPerformed( e );
  }

  public String getTitle() {
    return label.getText();
  }

  public void setTitle( final String title ) {
    label.setText( title );
  }

  public String getRawTabName() {
    return rawTabName;
  }

  public void setRawTabName( final String rawTabName ) {
    this.rawTabName = rawTabName;
  }

  public String recomputeTabName() {
    final JTabbedPane editorPane = getReportEditorPane();
    final int count = editorPane.getTabCount();
    int found = 0;
    for ( int i = 0; i < count; i++ ) {
      final Component at = editorPane.getTabComponentAt( i );
      if ( at == this ) {
        if ( found == 0 ) {
          return rawTabName;
        } else {
          return rawTabName + "<" + found + ">";
        }
      } else if ( at instanceof TabRenderer ) {
        final TabRenderer otherRenderer = (TabRenderer) at;
        if ( rawTabName.equals( otherRenderer.rawTabName ) ) {
          found += 1;
        }
      } else {
        if ( rawTabName.equals( editorPane.getTitleAt( i ) ) ) {
          found += 1;
        }
      }
    }
    return rawTabName;
  }
}
