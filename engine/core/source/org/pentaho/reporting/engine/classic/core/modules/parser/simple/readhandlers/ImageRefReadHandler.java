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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ContentElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.SAXException;

public class ImageRefReadHandler extends AbstractImageElementReadHandler
{
  private ContentElementFactory elementFactory;
  private static final String SRC_ATT = "src";

  public ImageRefReadHandler()
  {
    this.elementFactory = new ContentElementFactory();
  }

  /**
   * Starts parsing.
   *
   * @param atts the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final PropertyAttributes atts)
      throws SAXException
  {
    super.startParsing(atts);
    handleSource(atts);
  }

  private void handleSource(final PropertyAttributes atts)
  {
    final ResourceManager resourceManager = getRootHandler().getResourceManager();
    elementFactory.setBaseURL(resourceManager.toURL(getRootHandler().getContext()));
    elementFactory.setContent(atts.getValue(getUri(), ImageRefReadHandler.SRC_ATT));
  }


  protected ElementFactory getElementFactory()
  {
    return elementFactory;
  }
}
