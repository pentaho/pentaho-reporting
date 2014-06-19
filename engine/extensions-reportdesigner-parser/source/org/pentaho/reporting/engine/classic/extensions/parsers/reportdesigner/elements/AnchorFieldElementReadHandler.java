/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.templates.AnchorFieldTemplate;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.xml.sax.SAXException;

public class AnchorFieldElementReadHandler extends AbstractReportElementReadHandler
{
  private Element element;
  private AnchorFieldTemplate anchorFieldTemplate;

  public AnchorFieldElementReadHandler()
  {
    this.element = new Element();
    this.element.setElementType(LegacyType.INSTANCE);
    this.anchorFieldTemplate = new AnchorFieldTemplate();
    this.element.setDataSource(anchorFieldTemplate);
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    final String fieldName = getResult().getProperty("fieldName");
    anchorFieldTemplate.setField(fieldName);
  }

  protected Element getElement()
  {
    return element;
  }
}
