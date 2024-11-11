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


package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Reads the character width and height (without kerning). If the codepoint is a compound codepoint of an grapheme
 * cluster, return the maximum of all previously returned sizes of that cluster.
 *
 * @author Thomas Morgner
 */
public interface FontSizeProducer extends ClassificationProducer {
  public GlyphMetrics getCharacterSize( int codePoint,
                                        GlyphMetrics dimension );
}
