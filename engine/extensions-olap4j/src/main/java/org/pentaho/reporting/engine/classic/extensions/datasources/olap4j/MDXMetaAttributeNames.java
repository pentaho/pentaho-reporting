/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
