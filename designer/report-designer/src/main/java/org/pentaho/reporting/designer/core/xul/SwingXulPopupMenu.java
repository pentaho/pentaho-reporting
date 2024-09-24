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

package org.pentaho.reporting.designer.core.xul;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulMenuseparator;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.SwingElement;

import javax.swing.*;

/**
 * this is a very minimal and definitely not standard-conforming popup menu.
 */
public class SwingXulPopupMenu extends SwingElement implements XulPopup {
  private JPopupMenu menu;

  public SwingXulPopupMenu( final Element self, final XulComponent parent, final XulDomContainer domContainer,
                            final String tagName ) {
    super( tagName );

    menu = new JPopupMenu();
    setManagedObject( menu );

  }

  /**
   * Defeats layout calls. Useful for bulk updates.
   *
   * @param suppress
   */
  public void suppressLayout( final boolean suppress ) {

  }

  public void layout() {
    this.menu.removeAll();
    for ( final XulComponent comp : getChildNodes() ) {
      if ( comp.isVisible() == false ) {
        continue;
      }

      if ( comp instanceof XulMenuseparator ) {
        this.menu.addSeparator();
      } else if ( comp instanceof SwingXulPopupMenu ) {
        this.menu.add( (JMenu) comp.getManagedObject() );
      } else {
        this.menu.add( (JMenuItem) comp.getManagedObject() );
      }
    }
    initialized = true;
  }

  @Deprecated
  public void addComponent( final XulComponent c ) {
    addChild( c );
  }

  public void addComponentAt( final XulComponent component, final int idx ) {
    addChildAt( component, idx );
  }

  public void removeComponent( final XulComponent component ) {
    removeChild( component );
  }

  public boolean isDisabled() {
    return !menu.isEnabled();
  }

  public void setDisabled( final boolean disabled ) {
    menu.setEnabled( !disabled );
  }

}
