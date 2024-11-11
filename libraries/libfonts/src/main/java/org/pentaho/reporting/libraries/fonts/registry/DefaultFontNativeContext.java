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


package org.pentaho.reporting.libraries.fonts.registry;

import java.io.Serializable;

public class DefaultFontNativeContext implements Serializable, FontNativeContext {
  private boolean nativeBold;
  private boolean nativeItalics;

  public DefaultFontNativeContext( final boolean nativeBold, final boolean nativeItalics ) {
    this.nativeBold = nativeBold;
    this.nativeItalics = nativeItalics;
  }

  public boolean isNativeBold() {
    return nativeBold;
  }

  public boolean isNativeItalics() {
    return nativeItalics;
  }
}
