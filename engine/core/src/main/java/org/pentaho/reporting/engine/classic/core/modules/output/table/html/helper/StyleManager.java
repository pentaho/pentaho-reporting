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



package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.io.IOException;
import java.io.Writer;

/**
 * This class manages all existing styles. There are two implementations, an inline-manager and a style-sheet manager.
 *
 * @author Thomas Morgner
 */
public interface StyleManager {
  /**
   * Updates the given attribute-List according to the current style rules.
   *
   * @param styleBuilder
   * @param attributeList
   * @return the modified attribute list.
   */
  public AttributeList updateStyle( StyleBuilder styleBuilder, AttributeList attributeList );

  /**
   * Returns the global stylesheet, or null, if no global stylesheet was built.
   *
   * @return
   * @deprecated This method is slower than writing to the stream directly. Do not use it.
   */
  public String getGlobalStyleSheet();

  public void write( Writer writer ) throws IOException;
}
