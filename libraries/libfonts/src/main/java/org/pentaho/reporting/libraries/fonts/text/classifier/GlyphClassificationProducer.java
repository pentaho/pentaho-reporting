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

package org.pentaho.reporting.libraries.fonts.text.classifier;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 26.06.2006, 16:34:52
 *
 * @author Thomas Morgner
 */
public interface GlyphClassificationProducer extends ClassificationProducer {
  public static final int SPACE_CHAR = 0;
  public static final int LETTER = 1;

  public int getClassification( int codepoint );

  public void reset();
}
