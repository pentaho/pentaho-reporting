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

package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * User: Martin Date: 11.03.2005 Time: 10:11:41
 */
public class Category {
  public static final String MINIMIZED_PROPERTY = "minimized";

  private PropertyChangeSupport propertyChangeSupport;
  private ImageIcon iconBig;
  private ImageIcon iconSmall;
  private String title;
  private JComponent mainComponent;
  private boolean minimized;

  public Category( final ImageIcon iconBig,
                   final String title,
                   final JComponent mainComponent ) {
    this( iconBig, iconBig, title, mainComponent, false );
  }

  public Category( final ImageIcon iconBig,
                   final ImageIcon iconSmall,
                   final String title,
                   final JComponent mainComponent,
                   final boolean minimized ) {
    this.minimized = minimized;
    this.propertyChangeSupport = new PropertyChangeSupport( this );
    this.iconBig = iconBig;
    this.iconSmall = iconSmall;
    this.title = title;
    this.mainComponent = mainComponent;
  }

  public boolean isMinimized() {
    return minimized;
  }

  public void setMinimized( final boolean minimized ) {
    final boolean oldMinimized = this.minimized;
    this.minimized = minimized;
    propertyChangeSupport.firePropertyChange( MINIMIZED_PROPERTY, oldMinimized, minimized );
  }

  public ImageIcon getIconBig() {
    return iconBig;
  }

  public ImageIcon getIconSmall() {
    return iconSmall;
  }

  public String getTitle() {
    return title;
  }

  public JComponent getMainComponent() {
    return mainComponent;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }
}
