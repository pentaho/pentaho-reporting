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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice;

import java.net.URL;
import java.util.GregorianCalendar;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Article;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Customer;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model.Invoice;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class SimpleInvoiceDemoHandler extends AbstractXmlDemoHandler
{
  private InvoiceTableModel data;

  public SimpleInvoiceDemoHandler()
  {
    data = createDataModel();
  }

  private InvoiceTableModel createDataModel()
  {
    final Customer customer =
        new Customer("Will", "Snowman", "Mr.", "12 Federal Plaza",
            "12346", "AnOtherTown", "Lilliput");
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

    final InvoiceTableModel data = new InvoiceTableModel();
    data.addInvoice(invoice);
    return data;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("invoice.html", SimpleInvoiceDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("invoice.xml", SimpleInvoiceDemoHandler.class);
  }

  public String getDemoName()
  {
    return "Simple Invoice Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  public static void main(String[] args) throws ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
    SimpleInvoiceDemoHandler handler = new SimpleInvoiceDemoHandler();
    final MasterReport report = handler.createReport();
    final PreviewDialog dialog = new PreviewDialog();
    dialog.setReportJob(report);
    dialog.setSize(500, 500);
    dialog.setModal(true);
    dialog.setVisible(true);
    System.exit(0);

  }
}
