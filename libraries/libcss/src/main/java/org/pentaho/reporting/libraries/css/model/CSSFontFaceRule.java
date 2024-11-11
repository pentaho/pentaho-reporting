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


package org.pentaho.reporting.libraries.css.model;

/**
 * Defines a new font by either giving a set of properties which describe the font (so that the system can find a
 * matching local font) or by providing an URL from where to download the font file.
 * <p/>
 * For now, this is not implemented. Maybe later - this would add an interesting note to this library.
 *
 * @author Thomas Morgner
 */
public class CSSFontFaceRule extends CSSDeclarationRule {
  public CSSFontFaceRule( final StyleSheet parentStyle,
                          final StyleRule parentRule ) {
    super( parentStyle, parentRule );
  }
}
