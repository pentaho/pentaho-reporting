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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver;

import java.awt.print.Paper;
import java.io.IOException;

public interface PrinterDriver {

  /**
   * Gets the default character width in CPI.
   *
   * @return the default character width in CPI.
   */
  public float getCharactersPerInch();

  /**
   * Gets the default line height.
   *
   * @return the default line height.
   */
  public float getLinesPerInch();

  /**
   * Resets the printer and starts a new page. Prints the top border lines (if necessary).
   *
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  void startPage( final Paper paper, final String encoding ) throws IOException;

  /**
   * Ends the current page. Should print empty lines or an FORM_FEED command.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  void endPage( boolean overflow ) throws IOException;

  /**
   * Starts a new line.
   *
   * @throws java.io.IOException
   *           if an IOError occures.
   */
  void startLine() throws IOException;

  /**
   * Ends a new line.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if an IOError occures.
   */
  void endLine( boolean overflow ) throws IOException;

  /**
   * Prints a single text chunk at the given position on the current line. The chunk should not be printed, if an
   * previous chunk overlays this chunk.
   *
   * @param chunk
   *          the chunk that should be written
   * @throws java.io.IOException
   *           if an IO error occured.
   */
  public void printChunk( PlaintextDataChunk chunk ) throws IOException;

  /**
   * Prints an empty chunk. This is called for all undefined chunk-cells. The last defined font is used to print that
   * empty text.
   *
   * @throws java.io.IOException
   *           if an IOError occured.
   */
  public void printEmptyChunk( int count ) throws IOException;

  /**
   * Flushes the output stream.
   *
   * @throws java.io.IOException
   *           if an IOError occured.
   */
  public void flush() throws IOException;

  /**
   * Prints some raw content. This content is not processed in any way, so be very carefull.
   *
   * @param out
   *          the content that should be printed.
   */
  public void printRaw( byte[] out ) throws IOException;

}
