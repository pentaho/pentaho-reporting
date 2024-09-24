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
import org.pentaho.ui.xul.containers.XulHbox;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.SwingElement;
import org.pentaho.ui.xul.util.Orient;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SwingXulToolbar extends SwingElement implements XulHbox {
  private JToolBar container;

  public SwingXulToolbar( final Element self,
                          final XulComponent parent,
                          final XulDomContainer domContainer,
                          final String tagName ) {
    super( tagName );

    container = new JToolBar();
    container.setOpaque( false );

    setManagedObject( container );

    resetContainer();
  }

  /**
   * Defeats layout calls. Useful for bulk updates.
   *
   * @param suppress
   */
  public void suppressLayout( final boolean suppress ) {

  }

  @Deprecated
  public void addComponent( final XulComponent c ) {
    addChild( c );
  }

  public void resetContainer() {
    container.removeAll();
  }

  public Orient getOrientation() {
    return Orient.HORIZONTAL;
  }


  public void layout() {
    if ( getBgcolor() != null ) {
      container.setOpaque( true );
      container.setBackground( Color.decode( getBgcolor() ) );
    }

    final List<XulComponent> xulComponents = getChildNodes();
    for ( int i = 0; i < xulComponents.size(); i++ ) {
      final XulComponent component = xulComponents.get( i );
      final Object maybeComponent = component.getManagedObject();
      if ( maybeComponent == null || !( maybeComponent instanceof Component ) ) {
        continue;
      }
      if ( maybeComponent instanceof JSeparator ) {
        container.addSeparator();
      } else {
        container.add( (Component) maybeComponent );
      }
    }

    initialized = true;
  }

  @Deprecated
  public void addComponentAt( XulComponent component, int idx ) {
    addChildAt( component, idx );
  }

  @Deprecated
  public void removeComponent( XulComponent component ) {
    removeChild( component );
  }

  public void addChild( final Element e ) {
    super.addChild( e );
  }

  public void addChildAt( final Element c, final int pos ) {
    super.addChildAt( c, pos );
  }
}
