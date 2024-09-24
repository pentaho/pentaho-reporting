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
