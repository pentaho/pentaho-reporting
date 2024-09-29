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
