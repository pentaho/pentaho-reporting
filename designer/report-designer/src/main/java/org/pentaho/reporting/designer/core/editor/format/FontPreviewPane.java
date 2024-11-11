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
