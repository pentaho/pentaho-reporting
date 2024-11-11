/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.util;

/**
 * An exception that is thrown, if a report could not be defined. This encapsulates parse errors as well as runtime
 * exceptions caused by invalid setup code.
 *
 * @author: Thomas Morgner
 */
public class ReportDefinitionException extends Exception
{
  public ReportDefinitionException()
  {
  }

  public ReportDefinitionException(final String message, final Exception ex)
  {
    super(message, ex);
  }

  public ReportDefinitionException(final String message)
  {
    super(message);
  }
}
