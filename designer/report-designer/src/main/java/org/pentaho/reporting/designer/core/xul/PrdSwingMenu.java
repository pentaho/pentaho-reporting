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
