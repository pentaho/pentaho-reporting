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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.cards;

import java.net.URL;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A JFreeReport demo.
 *
 * @author Thomas Morgner.
 */
public class LeadingEmptyCardsDemoHandler extends AbstractXmlDemoHandler
{
  private TableModel data;

  /**
   * Default constructor.
   */
  public LeadingEmptyCardsDemoHandler()
  {
    final CardTableModel model = new CardTableModel();
    model.addCard(new NoPrintCard());
    model.addCard(new NoPrintCard());
    model.addCard(new NoPrintCard());
    model.addCard(new AdminCard("Jared", "Diamond", "NR123123", "login", "secret", new Date()));
    model.addCard(new FreeCard("NR123123", new Date()));
    model.addCard(new PrepaidCard("First Name", "Last Name", "NR123123"));
    model.addCard(new AccountCard("John", "Doe", "NR123123", "login", "secret"));
    model.addCard(new UserCard("Richard", "Helm", "NR123123", "login", "secret", new Date()));
    data = new WrappingTableModel(model, "C1_", "C2_");
  }

  public String getDemoName()
  {
    return "Card printing with leading empty cards";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("empty-usercards.html", LeadingEmptyCardsDemoHandler.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("usercards.xml", LeadingEmptyCardsDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final LeadingEmptyCardsDemoHandler handler = new LeadingEmptyCardsDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
