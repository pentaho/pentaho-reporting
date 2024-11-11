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

