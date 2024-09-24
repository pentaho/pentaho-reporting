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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.AbstractElementReadHandler;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 20.08.2007, 20:31:53
 *
 * @author Thomas Morgner
 */
public class ContentElementReadHandler extends AbstractElementReadHandler {
  private LabelElementFactory factory;

  public ContentElementReadHandler() {
    factory = new LabelElementFactory();
  }

  protected ElementFactory getElementFactory() {
    return factory;
  }

  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    factory.setName( atts.getValue( getUri(), "id" ) );
    factory.setText( atts.getValue( getUri(), "id" ) );
  }
}
