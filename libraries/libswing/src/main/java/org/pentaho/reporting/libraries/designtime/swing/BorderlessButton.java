/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BorderlessButton extends JButton {
  private class HoveringButtonHandler implements MouseListener {
    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked( final MouseEvent e ) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed( final MouseEvent e ) {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased( final MouseEvent e ) {

    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered( final MouseEvent e ) {
      if ( updateContentAreaFilled ) {
        setContentAreaFilled( true );
      }
      setBorderPainted( true );
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited( final MouseEvent e ) {
      if ( updateContentAreaFilled ) {
        setContentAreaFilled( false );
      }
      setBorderPainted( false );
    }
  }

  private boolean updateContentAreaFilled;

  /**
   * Creates a button with no set text or icon.
   */
  public BorderlessButton() {
    init();
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon the Icon image to display on the button
   */
  public BorderlessButton( final Icon icon ) {
    super( icon );
    init();
  }

  /**
   * Creates a button with text.
   *
   * @param text the text of the button
   */
  public BorderlessButton( final String text ) {
    super( text );
    init();
  }

  /**
   * Creates a button where properties are taken from the <code>Action</code> supplied.
   *
   * @param a the <code>Action</code> used to specify the new button
   * @since 1.3
   */
  public BorderlessButton( final Action a ) {
    super( a );
    init();
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text the text of the button
   * @param icon the Icon image to display on the button
   */
  public BorderlessButton( final String text, final Icon icon ) {
    super( text, icon );
    init();
  }

  public boolean isUpdateContentAreaFilled() {
    return updateContentAreaFilled;
  }

  public void setUpdateContentAreaFilled( final boolean updateContentAreaFilled ) {
    this.updateContentAreaFilled = updateContentAreaFilled;
  }

  protected void init() {
    setMargin( new Insets( 0, 0, 0, 0 ) );
    addMouseListener( new HoveringButtonHandler() );
    setBorderPainted( false );
    setContentAreaFilled( false );
    putClientProperty( "JButton.buttonType", "square" );
    putClientProperty( "JComponent.sizeVariant", "small" );
  }
}
