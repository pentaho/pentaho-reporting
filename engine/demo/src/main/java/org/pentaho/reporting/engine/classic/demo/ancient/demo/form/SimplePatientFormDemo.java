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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.form;

import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SimplePatientFormDemo extends AbstractXmlDemoHandler
{
  private PatientTableModel data;
  private GregorianCalendar calendar;

  public SimplePatientFormDemo()
  {
    calendar = new GregorianCalendar();

    final Patient johnDoe = new Patient("John Doe", "12 Nowhere Road", "Anytown",
        "1234-5678-AB12", "Greedy Health Care Corp.",
        "Symptoms - Weeping and RSI caused by hours of tearing up holiday photos. \n" +
            "Cause - Someone richer, younger and thinner than the patient. \n" +
            "Diagnostics - Broken Heart Disease");
    johnDoe.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "Initial consulting of the doctor", "-", "done"));
    johnDoe.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "X-Ray the patients chest", "-", "done"));
    johnDoe.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "Psychiatrist consulting", "-", "failed"));
    johnDoe.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "2nd X-Ray scan", "-", "done"));
    johnDoe.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "Two Surgeons open the chest and gently mend " +
                "the heart whilst holding their breath.", "anesthetics", "sucess"));
    johnDoe.setLevel("totally healed");

    final Patient kane = new Patient
        ("Kane, (First name not known)", "United States commercial starship Nostromo",
            "-", "4637-1345-NO123", "Aurora Mining Corp.",
            "Cause - Face huggers equipped with intelligent alien blood.\n" +
                "Symptoms - Gradual alien metamorphosis and desire to destroy our cities. \n" +
                "Diagnostics - Alien DNA");
    kane.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "Initial consulting of the doctor", "-", "failure"));
    kane.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "X-Ray Scan", "-", "failure"));
    kane.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "mechanically removing the DNA , cleaning of alien elements " +
                "and replacing quickly with clean DNA.", "-", "success"));
    kane.addTreament(new Treatment
        (createDate(12, 10, 1999),
            "balanced diet", "-", "failed"));
    kane.setAllergy("fast food");
    kane.setLevel("Alien escaped and killed the patient.");

    data = new PatientTableModel();
    data.addPatient(johnDoe);
    data.addPatient(kane);

  }

  private Date createDate(final int year, final int month, final int day)
  {
    calendar.clear();
    calendar.set(year, month - 1, day);
    return calendar.getTime();
  }

  public String getDemoName()
  {
    return "Patient Form Demo";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("form.html", SimplePatientFormDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(this.data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("patient.xml", SimplePatientFormDemo.class);
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

    final SimplePatientFormDemo handler = new SimplePatientFormDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
