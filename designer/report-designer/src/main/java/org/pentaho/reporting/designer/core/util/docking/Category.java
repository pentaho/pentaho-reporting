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
