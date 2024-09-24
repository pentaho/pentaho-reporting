/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.keys.content;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 01.12.2005, 17:50:36
 *
 * @author Thomas Morgner
 */
public class ContentValues {
  public static final CSSConstant OPEN_QUOTE = new CSSConstant( "open-quote" );
  public static final CSSConstant CLOSE_QUOTE = new CSSConstant( "close-quote" );
  public static final CSSConstant NO_OPEN_QUOTE = new CSSConstant( "no-open-quote" );
  public static final CSSConstant NO_CLOSE_QUOTE = new CSSConstant( "no-close-quote" );

  public static final CSSConstant CONTENTS = new CSSConstant( "contents" );
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant INHIBIT = new CSSConstant( "inhibit" );

  public static final CSSConstant FOOTNOTE = new CSSConstant( "footnote" );
  public static final CSSConstant ENDNOTE = new CSSConstant( "endnote" );
  public static final CSSConstant SECTIONNOTE = new CSSConstant( "sectionote" );
  public static final CSSConstant LISTITEM = new CSSConstant( "list-item" );
  public static final CSSConstant DOCUMENT_URL = new CSSConstant( "document-url" );

  private ContentValues() {
  }
}
