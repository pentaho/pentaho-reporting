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


package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 25.07.2006, 18:25:13
 *
 * @author Thomas Morgner
 */
public class DefaultLanguageClassifier implements LanguageClassifier {

  private int lastScript;

  public DefaultLanguageClassifier() {
  }

  public int getScript( final int codepoint ) {
    return 0;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
