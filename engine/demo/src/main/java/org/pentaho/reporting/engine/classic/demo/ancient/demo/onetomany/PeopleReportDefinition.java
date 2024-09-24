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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.PageOfPagesFunction;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

public class PeopleReportDefinition
{
  private MasterReport report;

  public PeopleReportDefinition()
  {
    report = new MasterReport();
    report.setName("People Report (API)");
    configurePeopleGroup();
    configureRecordGroup();

    // now the groups ... configured by an external class (as if it would be included)
    final ActivityReportDefinition activityDef = new ActivityReportDefinition(report);
    activityDef.configure();

    final LunchReportDefinition lunchDef = new LunchReportDefinition(report);
    lunchDef.configure();

    final OfficeReportDefinition officeDef = new OfficeReportDefinition(report);
    officeDef.configure();

    configurePageHeader();
    configurePageFooter();
    configureFunctions();
  }

  private void configureFunctions()
  {
    final PageOfPagesFunction pageFunction = new PageOfPagesFunction();
    pageFunction.setName("pageXofY");
    pageFunction.setFormat("Page {0} of {1}");
    report.addExpression(pageFunction);

  }

  private void configurePeopleGroup()
  {
    final RelationalGroup group = new RelationalGroup();
    group.setName("person-group");
    group.addField("person.name");

    LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    labelFactory.setMinimumSize(new FloatDimension(160, 12));
    labelFactory.setText("Name:");
    group.getHeader().addElement(labelFactory.createElement());

    TextFieldElementFactory textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("person.name");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(170, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    group.getHeader().addElement(textFieldFactory.createElement());

    labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(0, 15));
    labelFactory.setMinimumSize(new FloatDimension(160, 12));
    labelFactory.setText("Address:");
    group.getHeader().addElement(labelFactory.createElement());

    textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("person.address");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(170, 15));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    group.getHeader().addElement(textFieldFactory.createElement());

    group.getFooter().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(15));

    report.addGroup(group);
  }

  private void configureRecordGroup()
  {
    final RelationalGroup group = new RelationalGroup();
    group.setName("record-group");
    group.addField("person.name");
    group.addField("recordType");

    report.addGroup(group);
  }

  private void configurePageHeader()
  {
    final PageHeader pageHeader = report.getPageHeader();
    final ElementStyleSheet style = pageHeader.getStyle();
    style.setStyleProperty(BandStyleKeys.DISPLAY_ON_FIRSTPAGE, Boolean.TRUE);
    style.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(24));
    style.setStyleProperty(TextStyleKeys.FONT, "Serif");
    style.setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
    style.setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, new Color(0xAFAFAF));


    pageHeader.addElement
        (HorizontalLineElementFactory.createHorizontalLine
            (18, null, new BasicStroke(1)));

    final DateFieldElementFactory elementFactory = new DateFieldElementFactory();
    elementFactory.setAbsolutePosition(new Point2D.Float(-50, 0));
    elementFactory.setMinimumSize(new FloatDimension(-50, -100));
    elementFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    elementFactory.setHorizontalAlignment(ElementAlignment.RIGHT);
    elementFactory.setFormatString("d-MMM-yyyy");
    elementFactory.setFieldname("report.date");
    pageHeader.addElement(elementFactory.createElement());
  }

  private void configurePageFooter()
  {
    final PageFooter pageFooter = report.getPageFooter();
    final ElementStyleSheet style = pageFooter.getStyle();
    style.setStyleProperty(BandStyleKeys.DISPLAY_ON_FIRSTPAGE, Boolean.TRUE);
    style.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(24));
    style.setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, new Color(0xAFAFAF));
    pageFooter.addElement
        (HorizontalLineElementFactory.createHorizontalLine
            (0, null, new BasicStroke(1)));

    final TextFieldElementFactory elementFactory = new TextFieldElementFactory();
    elementFactory.setAbsolutePosition(new Point2D.Float(0, 4));
    elementFactory.setMinimumSize(new FloatDimension(-100, -100));
    elementFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    elementFactory.setHorizontalAlignment(ElementAlignment.RIGHT);
    elementFactory.setFieldname("pageXofY");
    report.getPageFooter().addElement(elementFactory.createElement());
  }


  public MasterReport getReport()
  {
    return report;
  }
}
