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

package demo;

import java.io.File;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class ParseDemo
{
  public static void main(String[] args) throws ResourceException
  {
    ClassicEngineBoot.getInstance().start();

    final File text = new File("/home/src/pentaho-report-designer/rd-1.7-rc1/samples/Invoice.report");
    final ResourceManager resManager = new ResourceManager();
    resManager.registerDefaults();

    final Resource directly = resManager.createDirectly(text, MasterReport.class);
    final MasterReport resource = (MasterReport) directly.getResource();

    final PreviewDialog dialog = new PreviewDialog(resource);
    dialog.setModal(true);
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }
}
