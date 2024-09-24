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

public interface PrinterSpecification {

  public String getDisplayName();

  /**
   * Returns the name of the encoding mapping. This is usually the same as the printer model name.
   *
   * @return the printer model.
   */
  public String getName();

  /**
   * Checks whether the given Java-encoding is supported.
   *
   * @param encoding
   *          the java encoding that should be mapped into a printer specific encoding.
   * @return true, if there is a mapping, false otherwise.
   */
  public boolean isEncodingSupported( String encoding );

  /**
   * Returns the encoding definition for the given java encoding.
   *
   * @param encoding
   *          the java encoding that should be mapped into a printer specific encoding.
   * @return the printer specific encoding.
   * @throws IllegalArgumentException
   *           if the given encoding is not supported.
   */
  public PrinterEncoding getEncoding( String encoding );

  /**
   * Returns true, if a given operation is supported, false otherwise.
   *
   * @param operationName
   *          the operation, that should be performed
   * @return true, if the printer will be able to perform that operation, false otherwise.
   */
  public boolean isFeatureAvailable( String operationName );
}
