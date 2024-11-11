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


package org.pentaho.reporting.engine.classic.core.layout.richtext;

import org.pentaho.reporting.engine.classic.core.ReportElement;

/**
 * A worker that converts raw-objects into rich-text objects. The worker expects either a byte-array, a char-array, a
 * string or a native Document and returns the converted value (or the value unchanged if its not a rich-text object).
 *
 * @author Thomas Morgner.
 */
public interface RichTextConverter {
  public boolean isRecognizedType( final String mimeType );

  public Object convert( final ReportElement source, final Object value );
}
