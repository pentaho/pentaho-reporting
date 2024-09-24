/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
