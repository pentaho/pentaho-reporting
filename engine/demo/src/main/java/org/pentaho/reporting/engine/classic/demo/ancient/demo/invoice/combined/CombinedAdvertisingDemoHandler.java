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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.combined;

import java.net.URL;
import java.util.GregorianCalendar;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.JoiningTableModel;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.AdvertisingTableModel;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.InvoiceTableModel;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Advertising;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Article;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Customer;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Invoice;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class CombinedAdvertisingDemoHandler extends AbstractXmlDemoHandler
{
  private TableModel data;

  public CombinedAdvertisingDemoHandler()
  {
    data = initData();
  }

  public String getDemoName()
  {
    return "Combined Advertising Demo";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("combined-invoice.html", CombinedAdvertisingDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("joined.xml", CombinedAdvertisingDemoHandler.class);
  }

  private TableModel initData()
  {
    final Customer customer =
        new Customer("Will", "Snowman", "Mr.", "12 Federal Plaza",
            "12346", "AnOtherTown", "Lilliput");
    final InvoiceTableModel iData = initInvoiceData(customer);
    final AdvertisingTableModel aData = initAdData(customer);
    final JoiningTableModel joiningTableModel = new JoiningTableModel();
    joiningTableModel.addTableModel("invoice", iData);
    joiningTableModel.addTableModel("ad", aData);
    return joiningTableModel;
  }

  private InvoiceTableModel initInvoiceData(final Customer customer)
  {
    final GregorianCalendar gc = new GregorianCalendar(2000, 10, 23);
    final Invoice invoice = new Invoice(customer, gc.getTime(), "A-000-0123");

    final Article mainboard = new Article("MB.001", "Ancient Mainboard", 199.50f);
    final Article hardDisk = new Article
        ("HD.201", "Very Slow Harddisk", 99.50f, "No warranty");
    final Article memory = new Article("MEM.36", "Dusty RAM modules", 59.99f);
    final Article operatingSystem = new Article
        ("OS.36", "QDOS with C/PM compatibility module", 259.99f, "Serial #44638-444-123");
    invoice.addArticle(mainboard);
    invoice.addArticle(hardDisk);
    invoice.addArticle(memory);
    invoice.addArticle(memory);
    invoice.addArticle(operatingSystem);

    final InvoiceTableModel invoiceData = new InvoiceTableModel();
    invoiceData.addInvoice(invoice);
    return invoiceData;
  }

  private AdvertisingTableModel initAdData(final Customer customer)
  {
    final GregorianCalendar gc = new GregorianCalendar(2000, 10, 23);
    final Advertising ad = new Advertising(customer, gc.getTime(), "A-000-0123");

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

    final AdvertisingTableModel adData = new AdvertisingTableModel();
    adData.addAdvertising(ad);
    return adData;
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

}
