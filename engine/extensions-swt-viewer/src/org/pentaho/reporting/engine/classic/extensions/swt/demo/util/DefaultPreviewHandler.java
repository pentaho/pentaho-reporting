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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.demo.util;

/**
 * ===========================================
 * JFreeReport : a free Java reporting library
 * ===========================================
 *
 * Project Info:  http://jfreereport.pentaho.org/
 *
 * (C) Copyright 2006, by Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * DefaultPreviewHandler.java
 * ------------
 * (C) Copyright 2006, by Pentaho Corporation.
 */

import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.DemoFrontend;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The DefaultPreviewHandler creates a PreviewDialog for the current report.
 *
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class DefaultPreviewHandler implements PreviewHandler
{
  private static final Log logger = LogFactory.getLog(DefaultPreviewHandler.class);
  private InternalDemoHandler handler;

  public DefaultPreviewHandler(final InternalDemoHandler handler)
  {
    this.handler = handler;
  }

  /**
   * Handler method called by the preview action. This method should perform all
   * operations to preview the report.
   */
  public void attemptPreview(final Shell shell)
  {
    try
    {
      final MasterReport report = handler.createReport();   
      final PreviewDialog preview = new PreviewDialog(shell, report);
      preview.setBlockOnOpen(true);
      preview.open();
    }
    catch (ReportDefinitionException e)
    {
      logger.error("Unable to create the report; report definition contained errors.", e);
      AbstractDemoFrame.showExceptionDialog("report.definitionfailure", e);
    }
  }
}
