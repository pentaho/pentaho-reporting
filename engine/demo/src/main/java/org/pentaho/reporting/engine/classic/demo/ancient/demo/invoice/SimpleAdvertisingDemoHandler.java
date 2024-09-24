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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice;

import java.net.URL;
import java.util.Date;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Advertising;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Article;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Customer;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class SimpleAdvertisingDemoHandler extends AbstractXmlDemoHandler
{
  private AdvertisingTableModel data;

  public SimpleAdvertisingDemoHandler()
  {
    data = createDataModel();
  }

  public String getDemoName()
  {
    return "Simple Advertising Demo";
  }

  private AdvertisingTableModel createDataModel()
  {
    final Customer customer =
        new Customer("Will", "Snowman", "Mr.", "12 Federal Plaza",
            "12346", "AnOtherTown", "Lilliput");
    final Advertising ad = new Advertising(customer, new Date(), "A-000-0123");

    final Article mainboard = new Article("MB.A02", "ZUSE Z0001 Mainboard", 1299.50f);
    final Article hardDisk = new Article
        ("HD.201", "Sillicium Core HDD", 99.50f,
            "Even the babylonians used stone for long term document archiving, so why shouldn't you?");
    final Article memory = new Article("MEM.30", "ferrit core memory", 119.99f);
    final Article operatingSystem = new Article
        ("OS.36", "Windows XP", 259.99f, "Experience the world of tomorrow by spreading trojans today.");
    ad.addArticle(mainboard, 999.99d);
    ad.addArticle(hardDisk, 79.50);
    ad.addArticle(memory, 99.99f);
    ad.addArticle(operatingSystem, 199.99);

    final AdvertisingTableModel data = new AdvertisingTableModel();
    data.addAdvertising(ad);
    return data;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("advertising.html", SimpleAdvertisingDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("advertising.xml", SimpleAdvertisingDemoHandler.class);
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }
}
