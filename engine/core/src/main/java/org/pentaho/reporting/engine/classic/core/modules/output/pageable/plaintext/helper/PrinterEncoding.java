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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper;

public final class PrinterEncoding {
  private String displayName;
  private String encoding;
  private byte[] code;
  private String internalName;

  public PrinterEncoding( final String internalName, final String displayName, final String encoding, final byte[] code ) {
    if ( internalName == null ) {
      throw new NullPointerException();
    }
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    if ( code == null ) {
      throw new NullPointerException();
    }
    if ( displayName == null ) {
      this.displayName = internalName;
    } else {
      this.displayName = displayName;
    }
    this.internalName = internalName;
    this.encoding = encoding;
    this.code = new byte[code.length];
    System.arraycopy( code, 0, this.code, 0, code.length );
  }

  public byte[] getCode() {
    final byte[] retval = new byte[code.length];
    System.arraycopy( code, 0, retval, 0, code.length );
    return retval;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEncoding() {
    return encoding;
  }

  public String getInternalName() {
    return internalName;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof PrinterEncoding ) ) {
      return false;
    }

    final PrinterEncoding printerEncoding = (PrinterEncoding) o;

    if ( !internalName.equals( printerEncoding.internalName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return internalName.hashCode();
  }

  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PrinterEncoding{"
        + "internalName='" + internalName + '\'' + ", displayName='" + displayName + '\'' + ", encoding='" + encoding
        + '\'' + '}';
  }
}
