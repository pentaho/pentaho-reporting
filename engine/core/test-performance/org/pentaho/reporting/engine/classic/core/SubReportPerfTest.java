package org.pentaho.reporting.engine.classic.core;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.beans.ArrayValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.StringValueConverter;
import org.databene.contiperf.*;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;

public class SubReportPerfTest
{
  private static final Log logger = LogFactory.getLog(SubReportPerfTest.class);

  @Rule
  public ContiPerfRule i = new ContiPerfRule();

  public SubReportPerfTest()
  {
  }

  @Before
  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  private TableModel createFruitTableModel()
  {
    final String[] names = new String[]{"Id Number", "Cat", "Fruit"};
    final Object[][] data = new Object[][]{
        {"I1", "A", "Apple"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I4", "B", "Strawberry"},
    };
    return new DefaultTableModel(data, names);
  }

  public Element createLabelElement(final String label, final Rectangle2D bounds)
  {
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setName("LabelElement-" + label);
    labelFactory.setText(label);
    labelFactory.setFontName("Serif");
    labelFactory.setFontSize(new Integer(10));
    labelFactory.setBold(Boolean.FALSE);
    labelFactory.setHeight(new Float(bounds.getHeight()));
    labelFactory.setWidth(new Float(bounds.getWidth()));
    labelFactory.setWrap(TextWrap.WRAP);
    labelFactory.setAbsolutePosition(new Point2D.Double(bounds.getX(), bounds.getY()));
    labelFactory.setHorizontalAlignment(ElementAlignment.LEFT);
    labelFactory.setVerticalAlignment(ElementAlignment.TOP);
    final Element labelElement = labelFactory.createElement();

    return labelElement;
  }

  public MasterReport createReport()
  {
    final MasterReport report = new MasterReport();
//    final StaticDataFactory staticDataFactory = new StaticDataFactory();
//    report.setDataFactory(staticDataFactory);
//    report.setQuery(SubReportTest.class.getName() + "#createMainTableModel()");

    ArrayList<Element> labelList = buildLabelElementList(5, 5, 25, 25);
    for (Element element : labelList)
    {
      System.out.println("******* Adding element " + element.toString() + " to report item band");
      report.getPageHeader().addElement(element);
    }

    System.out.println("******* label list size: " + labelList.size());


    final SubReport subReport = new SubReport();
//    subReport.addInputParameter("c1", "c1");

    subReport.setQuery(SubReportTest.class.getName() + "#createSubReportTableModel(c1)");

    ArrayList<Element> subreportLabelList = buildLabelElementList(5, 5, 25, 25);
    for (Element element : subreportLabelList)
    {
      System.out.println("******* Adding element " + element.toString() + " to sub-report item band");
      subReport.getPageHeader().addElement(element);
    }

    report.getItemBand().addSubReport(subReport);

    return report;
  }

  private ArrayList<Element> buildLabelElementList(final int numRows, final int numElement, final int height, final int width)
  {
    final ArrayList<Element> elementList = new ArrayList<Element>();
    int currentX = 0;
    int currentY = 0;

    for (int row = 0; row < numRows; row++ )
    {
      for (int elemNum = 0; elemNum < numElement; elemNum++)
      {
        final Rectangle coordinates = new Rectangle(currentX, currentY, width, height);
        final String labelText = "Label-" + currentX + currentY;
        final Element label = createLabelElement(labelText, coordinates);
        System.out.println("********** creating label element at (x = " + currentX + ", y = " + currentY + ") for " + labelText);

        currentX += width;

        elementList.add(label);
      }

      currentY += height;
    }

    return elementList;
  }


  @PerfTest(invocations = 1, threads = 1)
//  @Required(max = 15000, average = 14000)
  @Test
  public void testSubReport() throws Exception
  {
    final MasterReport report = createReport();
    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand(report, report.getPageHeader(),
        new DefaultFontStorage(new MonospaceFontRegistry(10, 6)), false);
    ModelPrinter.INSTANCE.print(pageBox);

    final RenderBox labelElement = (RenderBox) MatchFactory.findElementByName(pageBox, "LabelElement-Label-00");
    assertEquals(StrictGeomUtility.toInternalValue(25), labelElement.getHeight());
    assertEquals(StrictGeomUtility.toInternalValue(0), labelElement.getY());

    RenderNode [] subreportNodes = MatchFactory.findElementsByAttribute(pageBox, AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE, SubReportType.INSTANCE);

    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable("fruit", createFruitTableModel());
    report.setQuery("Query Fruit");
    report.setDataFactory(tableDataFactory);

    DebugReportRunner.executeAll(report);
  }

  @PerfTest(invocations = 1, threads = 1)
  @Required(max = 15000, average = 14000)
  @Test
  public void testMultipleEmbeddedSubReports() throws Exception
  {
    final SubReport sr = new SubReport();
    sr.getReportHeader().addSubReport(new SubReport());
    sr.getReportHeader().addSubReport(new SubReport());

    final MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport(sr);
    report.getReportHeader().addSubReport(new SubReport());
    report.getReportHeader().addSubReport(new SubReport());

    DebugReportRunner.executeAll(report);
  }
}