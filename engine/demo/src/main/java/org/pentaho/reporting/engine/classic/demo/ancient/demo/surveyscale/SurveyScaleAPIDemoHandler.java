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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.surveyscale;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.MessageFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ItemCountFunction;
import org.pentaho.reporting.engine.classic.core.function.PageOfPagesFunction;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScale;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScaleExpression;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demo application that generates a report using the {@link org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScale}
 * class to present responses to a survey.
 *
 * @author David Gilbert
 */
public final class SurveyScaleAPIDemoHandler extends AbstractDemoHandler
{
  /**
   * The top page margin (in pts).
   */
  private static final float PAGE_MARGIN_TOP = 36.0f;

  /**
   * The bottom page margin (in pts).
   */
  private static final float PAGE_MARGIN_BOTTOM = 36.0f;

  /**
   * The left page margin (in pts).
   */
  private static final float PAGE_MARGIN_LEFT = 36.0f;

  /**
   * The right page margin (in pts).
   */
  private static final float PAGE_MARGIN_RIGHT = 36.0f;

  /**
   * The x-coordinate for the start of the printing area.
   */
  private static final float X0 = 0.0f;

  /**
   * The gap to the first column.
   */
  private static final float LEFT_GAP = 15.0f;

  /**
   * The gap after the last column.
   */
  private static final float RIGHT_GAP = 15.0f;

  /**
   * The x-coordinate of the first column.
   */
  private static final float X1 = X0 + LEFT_GAP;

  /**
   * The width of the first column.
   */
  private static final float C1_WIDTH = 220.0f;

  /**
   * The x-coordinate of the second column.
   */
  private static final float X2 = X1 + C1_WIDTH;

  /**
   * The width of the second column.
   */
  private static final float C2_WIDTH = 176.0f;

  /**
   * The x-coordinate of the third column.
   */
  private static final float X3 = X2 + C2_WIDTH;

  private static final float PRINT_WIDTH = (float) PageSize.A4.getWidth();
  /**
   * The width of the third column.
   */
  private static final float C3_WIDTH =
      (PRINT_WIDTH - LEFT_GAP - C1_WIDTH - C2_WIDTH - RIGHT_GAP) / 2.0f;

  private static final float X4 = X3 + C3_WIDTH;

  private static final float C4_WIDTH = C3_WIDTH;

  /**
   * The height of the boxes used in the column header.
   */
  private static final float COLUMN_HEADER_BOX_HEIGHT = 20.0f;

  /**
   * The top of the boxes used in the column header.
   */
  private static final float BOX_TOP = 100.0f;

  /**
   * The core data for the report.
   */
  private TableModel data;

  public SurveyScaleAPIDemoHandler()
  {
    data = new SurveyScaleDemoTableModel();
  }

  public String getDemoName()
  {
    return "Survey Scale Demo Report (API)";
  }

  /**
   * Creates a report format by calling API methods directly.
   *
   * @return A report.
   */
  public MasterReport createReport()
  {

    final MasterReport report = new MasterReport();
    report.setName("Survey Scale Demo Report");

    // use A4...
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper paper = pff.createPaper(PageSize.A4);
    pff.setBorders(paper, PAGE_MARGIN_TOP, PAGE_MARGIN_LEFT, PAGE_MARGIN_BOTTOM, PAGE_MARGIN_RIGHT);
    final PageFormat format = pff.createPageFormat(paper, PageFormat.PORTRAIT);
    report.setPageDefinition(new SimplePageDefinition(format));

    setupWatermark(report);
    setupPageHeader(report);
    //// REPORT GROUP /////////////////////////////////////////////////////////////////////////
    setupGroup(report);
    //// ITEM BAND ////////////////////////////////////////////////////////////////////////////
    setupItemBand(report);
    //// PAGE FOOTER //////////////////////////////////////////////////////////////////////////
    setupPageFooter(report);

    report.getParameterValues().put("RESPONDENT_NAME", "Dave");
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  private void setupPageFooter(final MasterReport report)
  {
    final Band pageFooter = report.getPageFooter();
    pageFooter.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(65));

    // add a horizontal line to set off the page footer
    pageFooter.addElement(HorizontalLineElementFactory.createHorizontalLine(0, Color.BLACK, null));

    final PageOfPagesFunction pageNofM = new PageOfPagesFunction();
    pageNofM.setName("PAGE_N_OF_M");
    pageNofM.setFormat("Page {0} of {1}");
    report.addExpression(pageNofM);

    final TextFieldElementFactory tff = new TextFieldElementFactory();
    tff.setName("PageIndicator");
    tff.setAbsolutePosition(new Point2D.Double(X4 + C4_WIDTH - 60.0, 50.0));
    tff.setMinimumSize(new FloatDimension(60.0f, 15.0f));
    tff.setFontName("Serif");
    tff.setItalic(Boolean.TRUE);
    tff.setFontSize(new Integer(8));
    tff.setHorizontalAlignment(ElementAlignment.RIGHT);
    tff.setFieldname("PAGE_N_OF_M");
    pageFooter.addElement(tff.createElement());

    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText("Copyright \u00A9 2004 Object Refinery Limited. All Rights Reserved.");
    labelFactory.setFontName("Serif");
    labelFactory.setItalic(Boolean.TRUE);
    labelFactory.setFontSize(new Integer(8));
    labelFactory.setAbsolutePosition(new Point2D.Double(X1, 50.0));
    labelFactory.setMinimumSize(new FloatDimension(444.0f, 15.0f));
    labelFactory.setHorizontalAlignment(ElementAlignment.LEFT);
    pageFooter.addElement(labelFactory.createElement());
  }

  private void setupItemBand(final MasterReport report)
  {
    final Band band = report.getItemBand();
    band.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));

    final ItemCountFunction icf = new ItemCountFunction();
    icf.setName("ITEM_COUNT");

    report.addExpression(icf);

    final TextFieldElementFactory factory2 = new TextFieldElementFactory();
    factory2.setFontName("Serif");
    factory2.setFontSize(new Integer(11));
    factory2.setBold(Boolean.FALSE);

    final NumberFieldElementFactory nf = new NumberFieldElementFactory();
    nf.setName("ItemNumberTextField");
    nf.setAbsolutePosition(new Point2D.Double(X1, 7.0));
    nf.setMinimumSize(new FloatDimension(25.0f, 16.0f));
    nf.setVerticalAlignment(ElementAlignment.TOP);
    nf.setFieldname("ITEM_COUNT");
    nf.setFormatString("#0'.'");
    band.addElement(nf.createElement());

    factory2.setName("ItemField");
    factory2.setAbsolutePosition(new Point2D.Double(X1 + 25.0, 7.0));
    factory2.setMinimumSize(new FloatDimension(C1_WIDTH - 25.0f, 16.0f));
    factory2.setDynamicHeight(Boolean.TRUE);
    factory2.setTrimTextContent(Boolean.TRUE);
    factory2.setFieldname("Item");
    band.addElement(factory2.createElement());

    final SurveyScaleExpression iaf1 = new SurveyScaleExpression(1, 5);
    iaf1.setName("Survey Response");
    iaf1.setField(0, "Your Response");
    iaf1.setField(1, "Average Response");

    report.addExpression(iaf1);

    final ContentFieldElementFactory f = new ContentFieldElementFactory();
    f.setFieldname("Survey Response");
    f.setMinimumSize(new FloatDimension(C2_WIDTH, 15.0f));
    f.setAbsolutePosition(new Point2D.Double(X2, 6.0));
    band.addElement(f.createElement());

    final NumberFieldElementFactory nfef = new NumberFieldElementFactory();
    nfef.setFontName("Serif");
    nfef.setFontSize(new Integer(11));
    nfef.setName("F1");
    nfef.setAbsolutePosition(new Point2D.Double(X3, 7.0));
    nfef.setMinimumSize(new FloatDimension(C3_WIDTH, 16.0f));
    nfef.setFieldname("Your Response");
    nfef.setFormatString("0.00");
    nfef.setHorizontalAlignment(ElementAlignment.CENTER);
    band.addElement(nfef.createElement());

    nfef.setName("F2");
    nfef.setAbsolutePosition(new Point2D.Double(X4, 7.0));
    nfef.setFieldname("Average Response");
    band.addElement(nfef.createElement());
  }

  private void setupGroup(final MasterReport report)
  {
    final RelationalGroup group = new RelationalGroup();
    group.setName("Category Group");
    group.addField("Category");

    final GroupHeader gh = group.getHeader();
    gh.setRepeat(true);
    gh.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(26));

    final TextFieldElementFactory factory1 = new TextFieldElementFactory();
    factory1.setName("CategoryTextField");
    factory1.setAbsolutePosition(new Point2D.Double(X1, 10.0));
    factory1.setMinimumSize(new FloatDimension(C1_WIDTH + C2_WIDTH + C3_WIDTH, 16.0f));
    factory1.setVerticalAlignment(ElementAlignment.TOP);
    factory1.setFieldname("Category");
    factory1.setFontName("SansSerif");
    factory1.setFontSize(new Integer(12));
    factory1.setBold(Boolean.TRUE);
    factory1.setDynamicHeight(Boolean.TRUE);
    factory1.setTrimTextContent(Boolean.TRUE);
    gh.addElement(factory1.createElement());

    factory1.setFieldname("Category Description");
    factory1.setBold(Boolean.FALSE);
    factory1.setAbsolutePosition(new Point2D.Double(X1, 26));
    factory1.setFontName("Serif");
    factory1.setFontSize(new Integer(11));
    gh.addElement(factory1.createElement());
    report.addGroup(group);
  }

  private void setupPageHeader(final MasterReport report)
  {
    // define the page header...
    final Band pageHeader = report.getPageHeader();
    pageHeader.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));

    // the main heading is just a fixed label centered on the page...
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText("Free / Open Source Software Survey");
    labelFactory.setFontName("SansSerif");
    labelFactory.setFontSize(new Integer(18));
    labelFactory.setBold(Boolean.TRUE);
    labelFactory.setAbsolutePosition(new Point2D.Double(X1, 10.0));
    labelFactory.setMinimumSize(new FloatDimension(C1_WIDTH + C2_WIDTH + C3_WIDTH + C4_WIDTH, 28.0f));
    labelFactory.setHorizontalAlignment(ElementAlignment.CENTER);
    pageHeader.addElement(labelFactory.createElement());

    // the following expression is used to format the manager name into a message string
    // that says 'Respondent:  <name>'.  The whole string can be centered on the page that way.
    // This expression expects to find a marked report property called 'MANAGER_NAME', look
    // in the attemptPreview() method to see how this is set up.

    // here is the element that displays the string calculated in the expression above.
    final MessageFieldElementFactory tfef = new MessageFieldElementFactory();
    tfef.setFormatString("Respondent: $(RESPONDENT_NAME)");
    tfef.setFontName("SansSerif");
    tfef.setFontSize(new Integer(12));
    tfef.setBold(Boolean.TRUE);
    tfef.setAbsolutePosition(new Point2D.Double(0.0, 38.0));
    tfef.setMinimumSize(new FloatDimension(PRINT_WIDTH, 14.0f));
    tfef.setHorizontalAlignment(ElementAlignment.CENTER);
    pageHeader.addElement(tfef.createElement());

    labelFactory.setAbsolutePosition(new Point2D.Double(X1, 58.0));
    labelFactory.setText(
        "Please note that the questions AND responses presented below were INVENTED for the the purpose of this demo report.  They are NOT real.");
    labelFactory.setFontName("Serif");
    labelFactory.setFontSize(new Integer(11));
    labelFactory.setBold(Boolean.FALSE);
    labelFactory.setItalic(Boolean.TRUE);
    pageHeader.addElement(labelFactory.createElement());

    // labels
    labelFactory.setFontName("SansSerif");
    labelFactory.setFontSize(new Integer(7));
    labelFactory.setItalic(Boolean.FALSE);
    labelFactory.setBold(Boolean.FALSE);
    labelFactory.setVerticalAlignment(ElementAlignment.BOTTOM);

    final float delta = C2_WIDTH / 5.0f;
    labelFactory.setText("Not Important");
    labelFactory.setAbsolutePosition(new Point2D.Double(X2, 70.0));
    labelFactory.setMinimumSize(new FloatDimension(delta, 30.0f));
    pageHeader.addElement(labelFactory.createElement());

    labelFactory.setText("Very Important");
    labelFactory.setAbsolutePosition(new Point2D.Double(X2 + 4 * delta, 70.0));
    labelFactory.setMinimumSize(new FloatDimension(delta, 30.0f));
    pageHeader.addElement(labelFactory.createElement());

    addBoxedLabelToBand(pageHeader, null, X1, BOX_TOP, C1_WIDTH, COLUMN_HEADER_BOX_HEIGHT,
        "SansSerif", 10, true, Color.black, new Color(220, 255, 220));
    addBoxedLabelToBand(pageHeader, null, X2, BOX_TOP, C2_WIDTH, COLUMN_HEADER_BOX_HEIGHT,
        "SansSerif", 10, true, Color.black, new Color(220, 255, 220));

    final SurveyScale scaleHeader = new SurveyScale(1, 5, null);
    scaleHeader.setDrawScaleValues(true);
    scaleHeader.setDrawTickMarks(false);
    scaleHeader.setScaleValueFont(new Font("SansSerif", Font.PLAIN, 9));
    report.getParameterValues().put("SCALE_HEADER", scaleHeader);

    final ContentFieldElementFactory dfef = new ContentFieldElementFactory();
    dfef.setName("ScaleHeaderElement");
    dfef.setAbsolutePosition(new Point2D.Double(X2, BOX_TOP));
    dfef.setFieldname("SCALE_HEADER");
    dfef.setMinimumSize(new FloatDimension(C2_WIDTH, COLUMN_HEADER_BOX_HEIGHT));
    pageHeader.addElement(dfef.createElement());

    addBoxedLabelToBand(pageHeader, "Your Response", X3, BOX_TOP, C3_WIDTH, COLUMN_HEADER_BOX_HEIGHT,
        "SansSerif", 8, false, Color.black, new Color(220, 255, 220));

    addBoxedLabelToBand(pageHeader, "Average Response", X3 + C3_WIDTH, BOX_TOP, C3_WIDTH, COLUMN_HEADER_BOX_HEIGHT,
        "SansSerif", 8, false, Color.black, new Color(220, 255, 220));
  }

  private void setupWatermark(final MasterReport report)
  {
    // use a watermark to draw a frame around the page...
    final Band watermarkBand = report.getWatermark();
    final RectangleElementFactory sef = new RectangleElementFactory();
    sef.setMinimumSize(new FloatDimension(-100.0f, -100));
    sef.setColor(Color.black);
    sef.setStroke(new BasicStroke(1.0f));
    sef.setShouldDraw(Boolean.TRUE);
    sef.setScale(Boolean.TRUE);
    sef.setKeepAspectRatio(Boolean.FALSE);
    watermarkBand.addElement(sef.createElement());
  }

  /**
   * A utility method that creates a boxed label and adds it to a band.
   *
   * @param band            the band.
   * @param label           the field name.
   * @param x               the x-coordinate within the band.
   * @param y               the y-coordinate within the band.
   * @param w               the width.
   * @param h               the height.
   * @param fontName        the font name.
   * @param fontSize        the font size.
   * @param bold            bold?
   * @param outlineColor    the outline color.
   * @param backgroundColor the background color.
   */
  private static void addBoxedLabelToBand(final Band band, final String label,
                                          final float x, final float y, final float w, final float h,
                                          final String fontName, final int fontSize, final boolean bold,
                                          final Color outlineColor,
                                          final Color backgroundColor)
  {
    // field text
    final LabelElementFactory f2 = new LabelElementFactory();
    f2.setAbsolutePosition(new Point2D.Double(x, y));
    f2.setMinimumSize(new FloatDimension(w, h));
    f2.setText(label);
    f2.setFontName(fontName);
    f2.setFontSize(new Integer(fontSize));
    f2.setBold((bold) ? Boolean.TRUE : Boolean.FALSE);
    f2.setHorizontalAlignment(ElementAlignment.CENTER);
    f2.setVerticalAlignment(ElementAlignment.MIDDLE);
    f2.setBackgroundColor(backgroundColor);
    f2.setBorderColor(outlineColor);
    f2.setBorderStyle(BorderStyle.SOLID);
    band.addElement(f2.createElement());

  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("survey-api.html", SurveyScaleAPIDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

}
