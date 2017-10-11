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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public abstract class AbstractImageElementReadHandler extends AbstractElementReadHandler {
  private static final String SCALE_ATT = "scale";
  private static final String KEEP_ASPECT_RATIO_ATT = "keepAspectRatio";

  protected AbstractImageElementReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    handleScale( atts );
    handleKeepAspectRatio( atts );
  }

  protected void handleScale( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractImageElementReadHandler.SCALE_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean scale = ParserUtil.parseBoolean( booleanValue, getLocator() );
    elementFactory.setScale( scale );
  }

  protected void handleKeepAspectRatio( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractImageElementReadHandler.KEEP_ASPECT_RATIO_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean keepAspectRatio = ParserUtil.parseBoolean( booleanValue, getLocator() );
    elementFactory.setKeepAspectRatio( keepAspectRatio );
  }
}
