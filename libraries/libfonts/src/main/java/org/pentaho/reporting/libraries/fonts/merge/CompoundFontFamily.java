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


package org.pentaho.reporting.libraries.fonts.merge;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

/**
 * Creation-Date: 20.07.2007, 18:54:28
 *
 * @author Thomas Morgner
 */
public class CompoundFontFamily implements FontFamily {
  private FontFamily base;
  private FontRegistry registry;

  public CompoundFontFamily( final FontFamily base,
                             final FontRegistry registry ) {
    if ( registry instanceof CompoundFontRegistry ) {
      throw new IllegalStateException();
    }
    this.base = base;
    this.registry = registry;
  }

  public FontRegistry getRegistry() {
    return registry;
  }

  public String getFamilyName() {
    return base.getFamilyName();
  }

  public String[] getAllNames() {
    return base.getAllNames();
  }

  public FontRecord getFontRecord( final boolean bold, final boolean italics ) {
    return new CompoundFontRecord( base.getFontRecord( bold, italics ), this, bold, italics );
  }
}
