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


package org.pentaho.reporting.libraries.fonts.text.whitespace;

/**
 * This class filters linebreaks in addition to the normal whitespace collapsing.
 *
 * @author Thomas Morgner
 */
public class CollapseWhiteSpaceFilter extends PreserveBreaksWhiteSpaceFilter {
  public CollapseWhiteSpaceFilter() {
  }

  protected boolean isLinebreak( final int codepoint ) {
    // we do not detect any linebreaks. They will be handled as if they were
    // ordinary whitespaces
    return false;
  }
}
