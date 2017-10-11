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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This creates a report similar to the one defined by report1.xml.
 *
 * @author Thomas Morgner
 */
public class CountryReportAPIDemoHandler extends AbstractDemoHandler
{
  private CountryDataTableModel data;


  /**
   * Default constructor.
   */
  public CountryReportAPIDemoHandler()
  {
    data = new CountryDataTableModel();
  }

  public String getDemoName()
  {
    return "Country Report Demo (API)";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("country-report-api.html", CountryReportAPIDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  /**
   * Creates the page header.
   *
   * @return the page header.
   */
  private PageHeader createPageHeader()
  {
    final PageHeader header = new PageHeader();
    header.setName("Page-header");
    header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(18));
    header.getStyle().setStyleProperty(TextStyleKeys.FONT, "Dialog");
    header.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_TOP_COLOR, new Color(0xafafaf));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_LEFT_COLOR, new Color(0xafafaf));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_COLOR, new Color(0xafafaf));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_RIGHT_COLOR, new Color(0xafafaf));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_TOP_WIDTH, new Float(1));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_LEFT_WIDTH, new Float(1));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float(1));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float(1));
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID);
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID);
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID);
    header.getStyle().setStyleProperty(ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID);
    header.setDisplayOnFirstPage(true);
    header.setDisplayOnLastPage(false);

    final DateFieldElementFactory factory = new DateFieldElementFactory();
    factory.setName("Date");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 14));
    factory.setHorizontalAlignment(ElementAlignment.RIGHT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFormatString("d-MMM-yyyy");
    factory.setFieldname("report.date");
    header.addElement(factory.createElement());

    final Element line = HorizontalLineElementFactory.createHorizontalLine(16);
    line.getStyle().setStyleProperty(ElementStyleKeys.STROKE, new BasicStroke(2));
    line.getStyle().setStyleProperty(ElementStyleKeys.PAINT, Color.decode("#CFCFCF"));
    header.addElement(line);
    return header;
  }

  /**
   * Creates a page footer.
   *
   * @return The page footer.
   */
  private PageFooter createPageFooter()
  {
    final PageFooter pageFooter = new PageFooter();
    pageFooter.setName("Page-Footer");
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(30));
    pageFooter.getStyle().setStyleProperty(TextStyleKeys.FONT, "Dialog");
    pageFooter.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_TOP_WIDTH, new Float(1));
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_LEFT_WIDTH, new Float(1));
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float(1));
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float(1));
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID);
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID);
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID);
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID);

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Page-Footer-Label");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 0));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.TOP);
    factory.setText("Some Text for the page footer");
    factory.setDynamicHeight(Boolean.TRUE);
    pageFooter.addElement(factory.createElement());
    return pageFooter;
  }

  /**
   * Creates the report footer.
   *
   * @return the report footer.
   */
  private ReportFooter createReportFooter()
  {
    final ReportFooter footer = new ReportFooter();
    footer.setName("Report-Footer");
    footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(48));
    footer.getStyle().setStyleProperty(TextStyleKeys.FONT, "Serif");
    footer.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(16));
    footer.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.TRUE);

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Report-Footer-Label");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 24));
    factory.setHorizontalAlignment(ElementAlignment.CENTER);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("*** END OF REPORT ***");
    footer.addElement(factory.createElement());
    return footer;
  }

  /**
   * Creates the report header.
   *
   * @return the report header.
   */
  private ReportHeader createReportHeader()
  {
    final ReportHeader header = new ReportHeader();
    header.setName("Report-Header");
    header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(48));
    header.getStyle().setStyleProperty(TextStyleKeys.FONT, "Serif");
    header.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(20));
    header.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.TRUE);

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Report-Header-Label");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 24));
    factory.setHorizontalAlignment(ElementAlignment.CENTER);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("LIST OF CONTINENTS BY COUNTRY");
    header.addElement(factory.createElement());
    return header;
  }


  /**
   * Creates the itemBand.
   *
   * @return the item band.
   */
  private ItemBand createItemBand()
  {
    final ItemBand items = new ItemBand();
    items.setName("Items");
    items.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(10));
    items.getStyle().setStyleProperty(TextStyleKeys.FONT, "Monospaced");
    items.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    items.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, Color.decode("#dfdfdf"));


    items.addElement(HorizontalLineElementFactory.createHorizontalLine
        (0, Color.decode("#DFDFDF"), new BasicStroke(0.1f)));
    items.addElement(HorizontalLineElementFactory.createHorizontalLine
        (10, Color.decode("#DFDFDF"), new BasicStroke(0.1f)));

    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName("Country Element");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(176, 10));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFieldname("Country");
    items.addElement(factory.createElement());

    factory = new TextFieldElementFactory();
    factory.setName("Code Element");
    factory.setAbsolutePosition(new Point2D.Float(180, 0));
    factory.setMinimumSize(new FloatDimension(76, 10));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFieldname("ISO Code");
    items.addElement(factory.createElement());

    final NumberFieldElementFactory nfactory = new NumberFieldElementFactory();
    nfactory.setName("Population Element");
    nfactory.setAbsolutePosition(new Point2D.Float(260, 0));
    nfactory.setMinimumSize(new FloatDimension(76, 10));
    nfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    nfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    nfactory.setNullString("<null>");
    nfactory.setFieldname("Population");
    nfactory.setFormatString("#,##0");
    items.addElement(nfactory.createElement());
    return items;
  }

  /**
   * Creates the function collection. The xml definition for this construct:
   * <p/>
   * <pre>
   * <functions>
   * <function name="sum" class="org.pentaho.reporting.engine.classic.core.function.ItemSumFunction">
   * <properties>
   * <property name="field">Population</property>
   * <property name="group">Continent Group</property>
   * </properties>
   * </function>
   * <function name="backgroundTrigger"
   * class="org.pentaho.reporting.engine.classic.core.function.ElementVisibilitySwitchFunction">
   * <properties>
   * <property name="element">background</property>
   * </properties>
   * </function>
   * </functions>
   * </pre>
   *
   * @return the functions.
   */
  private ExpressionCollection createFunctions()
  {
    final ExpressionCollection functions = new ExpressionCollection();

    final ItemSumFunction sum = new ItemSumFunction();
    sum.setName("sum");
    sum.setField("Population");
    sum.setGroup("Continent Group");
    functions.add(sum);

    final RowBandingFunction backgroundTrigger = new RowBandingFunction();
    backgroundTrigger.setName("backgroundTrigger");
    functions.add(backgroundTrigger);

    return functions;
  }

  /**
   * <pre>
   * <group name="Continent Group">
   * <groupheader height="18" fontname="Monospaced" fontstyle="bold" fontsize="9"
   * pagebreak="false">
   * <label name="Label 5" x="0" y="1" width="76" height="9" alignment="left">CONTINENT:</label>
   * <string-field name="Continent Element" x="96" y="1" width="76" height="9"
   * alignment="left"
   * fieldname="Continent"/>
   * <line name="line1" x1="0" y1="12" x2="0" y2="12" weight="0.5"/>
   * </groupheader>
   * <groupfooter height="18" fontname="Monospaced" fontstyle="bold" fontsize="9">
   * <label name="Label 6" x="0" y="0" width="450" height="12" alignment="left"
   * baseline="10">Population:</label>
   * <number-function x="260" y="0" width="76" height="12" alignment="right"
   * baseline="10"
   * format="#,##0" function="sum"/>
   * </groupfooter>
   * <fields>
   * <field>Continent</field>
   * </fields>
   * </group>
   * </pre>
   *
   * @return the continent group.
   */
  private RelationalGroup createContinentGroup()
  {
    final RelationalGroup continentGroup = new RelationalGroup();
    continentGroup.setName("Continent Group");
    continentGroup.addField("Continent");

    final GroupHeader header = new GroupHeader();
    header.getStyle().setStyleProperty(TextStyleKeys.FONT, "Monospaced");
    header.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    header.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.TRUE);
    header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));
    header.setName("Continent-Group-Header");

    LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Continent-Group-Header-Label");
    factory.setAbsolutePosition(new Point2D.Float(0, 1));
    factory.setMinimumSize(new FloatDimension(76, 9));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("CONTINENT:");
    header.addElement(factory.createElement());

    final TextFieldElementFactory tfactory = new TextFieldElementFactory();
    tfactory.setName("Continent-Group-Header Continent Element");
    tfactory.setAbsolutePosition(new Point2D.Float(96, 1));
    tfactory.setMinimumSize(new FloatDimension(76, 9));
    tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    tfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    tfactory.setNullString("<null>");
    tfactory.setFieldname("Continent");
    header.addElement(tfactory.createElement());

    header.addElement(HorizontalLineElementFactory.createHorizontalLine(12, null, new BasicStroke(0.5f)));
    continentGroup.setHeader(header);

    final GroupFooter footer = new GroupFooter();
    footer.setName("Continent-Group-Footer");
    footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));
    header.getStyle().setStyleProperty(TextStyleKeys.FONT, "Monospaced");
    header.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    header.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.TRUE);

    factory = new LabelElementFactory();
    factory.setName("Continent-Group-Footer Label");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(100, 12));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("Population:");
    footer.addElement(factory.createElement());

    final NumberFieldElementFactory nfactory = new NumberFieldElementFactory();
    nfactory.setName("Continent-Group-Footer Sum");
    nfactory.setAbsolutePosition(new Point2D.Float(260, 0));
    nfactory.setMinimumSize(new FloatDimension(76, 12));
    nfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    nfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    nfactory.setNullString("<null>");
    nfactory.setFieldname("sum");
    nfactory.setFormatString("#,##0");
    footer.addElement(nfactory.createElement());
    continentGroup.setFooter(footer);
    return continentGroup;
  }

  /**
   * Creates the report.
   *
   * @return the constructed report.
   */
  public MasterReport createReport()
  {
    final MasterReport report = new MasterReport();
    report.setName("Sample Report 1");
    report.setReportFooter(createReportFooter());
    report.setReportHeader(createReportHeader());
    report.setPageFooter(createPageFooter());
    report.setPageHeader(createPageHeader());
    report.addGroup(createContinentGroup());

    final GroupDataBody dataBody = (GroupDataBody) report.getChildElementByType(GroupDataBodyType.INSTANCE);
    dataBody.setItemBand(createItemBand());
    
    report.setExpressions(createFunctions());
    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encoding", "Identity-H");
    report.setDataFactory(new TableDataFactory
        ("default", data));


    try
    {
      Watermark watermark = report.getWatermark();
      watermark.setName("WaterMark");

      final URL resource = getClass().getResource("earth.png");
      final ContentElementFactory img1 = new ContentElementFactory();
      img1.setContent(resource);
      img1.setMinimumSize(new FloatDimension(500, 500));
      img1.setAbsolutePosition(new Point2D.Float(0, 0));
      img1.setScale(Boolean.TRUE);
      watermark.addElement(img1.createElement());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }


    return report;
  }
}
