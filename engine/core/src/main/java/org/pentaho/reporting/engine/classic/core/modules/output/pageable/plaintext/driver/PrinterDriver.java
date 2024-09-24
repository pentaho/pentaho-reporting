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
