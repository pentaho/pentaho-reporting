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

package org.pentaho.reporting.designer.core.widgets;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A small wrapper around a Titled border.
 *
 * @author Thomas Morgner.
 */
public class JSeparatorLabel extends JPanel {
  private TitledBorder borderBackend;

  public JSeparatorLabel( final String text ) {
    borderBackend = new TitledBorder( new MatteBorder( 1, 0, 0, 0, SystemColor.controlDkShadow ), text );
    setBorder( borderBackend );
  }

  public String getText() {
    return borderBackend.getTitle();
  }

  public void setText( final String text ) {
    this.borderBackend.setTitle( text );
  }

  public Border getTopBorder() {
    return this.borderBackend.getBorder();
  }

  public void setTitleBorder( final Border border ) {
    this.borderBackend.setBorder( border );
  }

  public Border getTitleBorder() {
    return borderBackend.getBorder();
  }

  public int getTitlePosition() {
    return borderBackend.getTitlePosition();
  }

  public void setTitlePosition( final int titlePosition ) {
    borderBackend.setTitlePosition( titlePosition );
  }

  public int getTitleJustification() {
    return borderBackend.getTitleJustification();
  }

  public void setTitleJustification( final int titleJustification ) {
    borderBackend.setTitleJustification( titleJustification );
  }

  public Font getTitleFont() {
    return borderBackend.getTitleFont();
  }

  public void setTitleFont( final Font titleFont ) {
    borderBackend.setTitleFont( titleFont );
  }

  public Color getTitleColor() {
    return borderBackend.getTitleColor();
  }

  public void setTitleColor( final Color titleColor ) {
    borderBackend.setTitleColor( titleColor );
  }
}
