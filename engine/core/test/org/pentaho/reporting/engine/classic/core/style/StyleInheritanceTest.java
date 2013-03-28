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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class StyleInheritanceTest extends TestCase
{
  public StyleInheritanceTest()
  {
  }

  public StyleInheritanceTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  private Element createLabel(final String text)
  {
    final Element element = new Element();
    element.setElementType(LabelType.INSTANCE);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(200));
    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    return element;
  }

  public void testStyleInheritance() throws Exception
  {
    MasterReport report = new MasterReport();
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.addElement(createLabel("Text"));

    ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 0));
  }
}