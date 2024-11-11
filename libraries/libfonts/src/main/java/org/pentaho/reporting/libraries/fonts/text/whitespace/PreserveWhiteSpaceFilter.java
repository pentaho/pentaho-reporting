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
 * Creation-Date: 11.06.2006, 20:18:00
 *
 * @author Thomas Morgner
 */
public class PreserveWhiteSpaceFilter implements WhiteSpaceFilter {
  public PreserveWhiteSpaceFilter() {
  }

  /**
   * Filters the whitespaces. This method returns '-1', if the whitespace should be removed from the stream; otherwise
   * it presents a replacement character. If the codepoint is no whitespace at all, the codepoint is returned
   * unchanged.
   *
   * @param codepoint
   * @return
   */
  public int filter( final int codepoint ) {
    return codepoint;
  }

  /**
   * Reset the filter to the same state as if the filter had been constructed but not used yet.
   */
  public void reset() {

  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
