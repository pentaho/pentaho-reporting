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

package org.pentaho.reporting.libraries.fonts.text;

/**
 * unicode standard).
 * <p/>
 * The language code returned by this class is an integer, which maps to one of the ISO 15924 numerical language codes.
 * If there is an Unicode script tag, for which there exists no corresponding ISO code, then negative numbers are
 * assigned in descending order.
 *
 * @author Thomas Morgner
 */
public interface LanguageClassifier extends ClassificationProducer {
  public int getScript( int codepoint );

  // Todo: Derive the language from the characters. (See "scripts.txt" in the

}
