/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.demo.util;

import java.net.URL;


/**
 * An XML demo handler offers generic support for reading the report definition from an XML file.
 *
 * @author Thomas Morgner
 */
public interface XmlDemoHandler extends InternalDemoHandler
{
  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource();
}
