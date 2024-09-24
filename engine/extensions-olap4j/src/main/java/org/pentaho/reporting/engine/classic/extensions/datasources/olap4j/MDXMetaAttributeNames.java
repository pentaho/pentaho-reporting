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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

/**
 * These attribute names are based on the MSDN-Article at http://msdn2.microsoft.com/en-us/library/ms145573.aspx
 *
 * @author : Thomas Morgner
 */
public class MDXMetaAttributeNames {
  public static final String NAMESPACE =
    "http://reporting.pentaho.org/namespaces/engine/meta-attributes/mdx";

  public static final String FONT_FLAGS = "FONT_FLAGS";
  public static final String FONTNAME = "FONT_NAME";
  public static final String FONTSIZE = "FONT_SIZE";

  public static final String FOREGROUND_COLOR = "FORE_COLOR";
  public static final String BACKGROUND_COLOR = "BACK_COLOR";

  public static final String FORMAT_STRING = "FORMAT_STRING";
  public static final String LANGUAGE = "LANGUAGE";

  public static final String MDX_ALL_MEMBER = "MDX::ALL_MEMBER";
  public static final String MDX_CALCULATED = "MDX::CALCULATED";
  public static final String MDX_HIDDEN = "MDX::HIDDEN";
  public static final String MDX_CAPTION = "MDX::CAPTION";
  public static final String MDX_DESCRIPTION = "MDX::DESCRIPTION";

  private MDXMetaAttributeNames() {
  }
}
