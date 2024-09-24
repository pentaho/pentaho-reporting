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

package org.pentaho.reporting.engine.classic.demo.util;

/**
 * A demo handler encapsulates all information that are necessary to completly execute a GUI demo.
 *
 * @author Thomas Morgner
 */
public interface DemoHandler
{
  public String getDemoName();

  public PreviewHandler getPreviewHandler();
}
