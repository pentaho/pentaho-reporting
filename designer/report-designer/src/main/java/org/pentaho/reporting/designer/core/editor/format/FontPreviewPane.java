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

package org.pentaho.reporting.designer.core.editor.format;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class FontPreviewPane extends JPanel {
  private boolean strikeThrough;
  private boolean underline;
  private boolean aliased;

  public FontPreviewPane() {
    setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
  }

  public boolean isStrikeThrough() {
    return strikeThrough;
  }

  public void setStrikeThrough( final boolean strikeThrough ) {
    this.strikeThrough = strikeThrough;
    repaint();
  }

  public boolean isUnderline() {
    return underline;
  }

  public void setUnderline( final boolean underline ) {
    this.underline = underline;
    repaint();
  }

  public boolean isAliased() {
    return aliased;
  }

  public void setAliased( final boolean aliased ) {
    this.aliased = aliased;
    repaint();
  }
}
