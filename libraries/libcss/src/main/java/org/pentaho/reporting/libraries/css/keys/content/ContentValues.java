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
