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
