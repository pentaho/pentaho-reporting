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


package org.pentaho.reporting.libraries.fonts.io;

import java.io.IOException;

/**
 * Creation-Date: 15.12.2005, 15:48:56
 *
 * @author Thomas Morgner
 */
public interface FontDataInputSource {
  public long getLength();

  public void readFullyAt( long position, byte[] buffer, int length )
    throws IOException;

  /**
   * Reads a single byte, returns -1 if the end of the stream as been reached.
   *
   * @param position
   * @return
   * @throws IOException
   */
  public int readAt( long position ) throws IOException;

  //  public int readAt (long position, byte[] buffer, int length) throws IOException;

  public void dispose();

  public String getFileName();

  public boolean equals( Object o );

  public int hashCode();
}
