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
import javax.swing.FocusManager;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DefaultHeaderComponent extends GradientPanel {
  private class FocusManagerChangeHandler implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( evt.getNewValue() instanceof Component ) {
        final Component component = (Component) evt.getNewValue();
        if ( SwingUtilities.isDescendingFrom( component, DefaultHeaderComponent.this ) ) {
          setFocused( true );
        } else {
          setFocused( false );
        }
      }
    }
  }

  private static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";
  private FocusManagerChangeHandler target;
  private double darkeningFactor;

  public DefaultHeaderComponent( final String category ) {
    this( category, new Insets( 1, 1, 1, 1 ), 0.9 );
  }

  public DefaultHeaderComponent( final String title, final Insets insets, final double darkeningFactor ) {
    this.darkeningFactor = darkeningFactor;

    setLayout( new BorderLayout() );
    setOpaque( false );
    setBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, getDarkerColor( getBackground() ) ) );
    setGradientColors( new Color[] { getDarkerColor( getBackground() ), getBackground() } );
    setDirection( GradientPanel.Direction.DIRECTION_LEFT );

    final JLabel headerLabel = new JLabel( title );
    headerLabel.setBorder( BorderFactory.createEmptyBorder( insets.top, insets.left, insets.bottom, insets.right ) );
    add( headerLabel, BorderLayout.CENTER );

    final FocusManager currentManager = FocusManager.getCurrentManager();
    target = new FocusManagerChangeHandler();
    currentManager.addPropertyChangeListener( PERMANENT_FOCUS_OWNER, target );
  }

  public void setFocused( final boolean b ) {
    if ( b == false ) {
      setGradientColors( new Color[] { getDarkerColor( getBackground() ), getBackground() } );
    } else {
      final Color bg = UIManager.getColor( "List.selectionBackground" ); // NON-NLS
      final Color fg = UIManager.getColor( "List.background" );// NON-NLS
      if ( bg != null && fg != null ) {
        setGradientColors( new Color[] { bg, fg } );
      }
    }
  }

  private Color getDarkerColor( final Color color ) {
    return new Color( Math.max( (int) ( color.getRed() * darkeningFactor ), 0 ),
      Math.max( (int) ( color.getGreen() * darkeningFactor ), 0 ),
      Math.max( (int) ( color.getBlue() * darkeningFactor ), 0 ) );
  }

  public void dispose() {
    FocusManager.getCurrentManager().removePropertyChangeListener( PERMANENT_FOCUS_OWNER, target );
  }
}

