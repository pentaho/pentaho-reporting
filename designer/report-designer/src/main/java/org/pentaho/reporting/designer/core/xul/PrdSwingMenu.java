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
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.components.XulMenuseparator;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.tags.SwingMenu;
import org.pentaho.ui.xul.swing.tags.SwingMenupopup;

import javax.swing.*;

public class PrdSwingMenu extends SwingMenu {
  private JMenu menu;

  public PrdSwingMenu( final Element self,
                       final XulComponent parent,
                       final XulDomContainer domContainer,
                       final String tagName ) {
    super( self, parent, domContainer, tagName );
    menu = (JMenu) getManagedObject();
  }


  public void layout() {
    this.menu.removeAll();
    for ( Element comp : getChildNodes() ) {
      if ( comp instanceof SwingMenupopup ) {
        for ( XulComponent compInner : comp.getChildNodes() ) {
          if ( compInner.isVisible() == false ) {
            continue;
          }

          if ( compInner instanceof XulMenuseparator ) {
            menu.addSeparator();
          } else if ( compInner instanceof SwingMenu ) {
            menu.add( (JMenu) compInner.getManagedObject() );
          } else if ( compInner instanceof XulMenuitem ) {
            menu.add( (JMenuItem) compInner.getManagedObject() );
          }
        }
      }
    }
    initialized = true;
  }

}
