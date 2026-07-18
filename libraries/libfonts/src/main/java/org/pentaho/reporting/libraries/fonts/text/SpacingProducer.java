/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.fonts.text;

/**
 * Reads spacing information. Spacing information defines the possible stretch points when performing text
 * justification.
 *
 * @author Thomas Morgner
 */
public interface SpacingProducer extends ClassificationProducer {
  public Spacing createSpacing( int codePoint );
}
