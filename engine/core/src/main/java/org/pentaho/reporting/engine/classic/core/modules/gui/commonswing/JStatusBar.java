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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.pentaho.reporting.engine.classic.core.modules.gui.common.DefaultIconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;

public class JStatusBar extends JComponent {
  public static final String STATUS_TYPE_PROPERTY = "statusType";
  public static final String STATUS_TEXT_PROPERTY = "statusText";
  public static final String ERROR_PROPERTY = "error";

  private JComponent otherComponents;
  private JLabel statusHolder;
  private IconTheme iconTheme;
  private StatusType statusType;

  public JStatusBar() {
    this( new DefaultIconTheme() );
  }

  public JStatusBar( final IconTheme theme ) {
    setLayout( new BorderLayout() );
    setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, UIManager.getDefaults().getColor( "controlShadow" ) ) ); //$NON-NLS-1$
    statusHolder = new JLabel( " " ); //$NON-NLS-1$
    statusHolder.setMinimumSize( new Dimension( 0, 20 ) );
    add( statusHolder, BorderLayout.CENTER );

    otherComponents = new JPanel();
    add( otherComponents, BorderLayout.EAST );
    this.iconTheme = theme;
    this.statusType = StatusType.NONE;
  }

  protected IconTheme getIconTheme() {
    return iconTheme;
  }

  public void setIconTheme( final IconTheme iconTheme ) {
    final IconTheme oldTheme = this.iconTheme;
    this.iconTheme = iconTheme;
    firePropertyChange( "iconTheme", oldTheme, iconTheme ); //$NON-NLS-1$

    if ( iconTheme == null ) {
      statusHolder.setIcon( null );
    } else {
      updateTypeIcon( getStatusType() );
    }
  }

  public JComponent getExtensionArea() {
    return otherComponents;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public String getStatusText() {
    return statusHolder.getText();
  }

  public void setStatusText( final String text ) {
    final String oldText = statusHolder.getText();
    this.statusHolder.setText( text );
    firePropertyChange( STATUS_TEXT_PROPERTY, oldText, text ); //$NON-NLS-1$
  }

  public void setStatusType( final StatusType type ) {
    if ( statusType == null ) {
      throw new NullPointerException();
    }
    final StatusType oldType = statusType;
    this.statusType = type;
    firePropertyChange( STATUS_TYPE_PROPERTY, oldType, type ); //$NON-NLS-1$
    updateTypeIcon( type );
  }

  public void setStatus( final StatusType type, final String text ) {
    this.statusType = type;
    updateTypeIcon( type );
    statusHolder.setText( text );
  }

  private void updateTypeIcon( final StatusType type ) {
    if ( iconTheme != null ) {
      if ( StatusType.ERROR.equals( type ) ) {
        final Icon res = getIconTheme().getSmallIcon( getLocale(), "statusbar.errorIcon" ); //$NON-NLS-1$
        statusHolder.setIcon( res );
      } else if ( StatusType.WARNING.equals( type ) ) {
        final Icon res = getIconTheme().getSmallIcon( getLocale(), "statusbar.warningIcon" ); //$NON-NLS-1$
        statusHolder.setIcon( res );
      } else if ( StatusType.INFORMATION.equals( type ) ) {
        final Icon res = getIconTheme().getSmallIcon( getLocale(), "statusbar.informationIcon" ); //$NON-NLS-1$
        statusHolder.setIcon( res );
      } else {
        final Icon res = getIconTheme().getSmallIcon( getLocale(), "statusbar.otherIcon" ); //$NON-NLS-1$
        statusHolder.setIcon( res );
      }
    }
  }

  public void clear() {
    setStatus( StatusType.NONE, " " ); //$NON-NLS-1$
  }

  /**
   * Gets the locale of this component.
   *
   * @return this component's locale; if this component does not have a locale, the locale of its parent is returned
   * @throws java.awt.IllegalComponentStateException
   *           if the <code>Component</code> does not have its own locale and has not yet been added to a containment
   *           hierarchy such that the locale can be determined from the containing parent
   * @see #setLocale
   * @since JDK1.1
   */
  public Locale getLocale() {
    try {
      return super.getLocale();
    } catch ( IllegalComponentStateException ice ) {
      return Locale.getDefault();
    }
  }
}
