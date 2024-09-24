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
import java.awt.geom.Point2D;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.HideElementByNameFunction;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

public class LunchReportDefinition
{
  private MasterReport report;

  public LunchReportDefinition(final MasterReport report)
  {
    this.report = report;
  }

  public void configure()
  {
    configureRecordGroup();
    configureItemBand();
    configureFunctions();
  }

  private void configureRecordGroup()
  {
    final Band b = new Band();
    b.setName("lunch");
    b.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.TRUE);
    b.getStyle().setStyleProperty(TextStyleKeys.FONT, "SansSerif");
    b.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(12));

    LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    labelFactory.setMinimumSize(new FloatDimension(200, 15));
    labelFactory.setText("Preferred Lunch Meals");
    b.addElement(labelFactory.createElement());

    labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(200, 0));
    labelFactory.setMinimumSize(new FloatDimension(-100, 15));
    labelFactory.setText("Rating");
    b.addElement(labelFactory.createElement());

    b.addElement(HorizontalLineElementFactory.createHorizontalLine
        (15, null, new BasicStroke(1)));

    final RelationalGroup group = report.getGroupByName("record-group");
    group.getHeader().addElement(b);
  }

  private void configureItemBand()
  {
    final Band b = new Band();
    b.setName("lunch");
    b.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.FALSE);
    b.getStyle().setStyleProperty(TextStyleKeys.FONT, "SansSerif");
    b.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));

    TextFieldElementFactory textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("lunch.Meal");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(200, 12));
    b.addElement(textFieldFactory.createElement());

    textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("lunch.Rating");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(200, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    textFieldFactory.setDynamicHeight(Boolean.TRUE);
    b.addElement(textFieldFactory.createElement());

    report.getItemBand().addElement(b);
  }

  private void configureFunctions()
  {
    final HideElementByNameFunction function = new HideElementByNameFunction();
    function.setName("hideLunch");
    function.setField("recordType");
    function.setElement("lunch");

    report.addExpression(function);
  }
}

