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

package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.engine.classic.wizard.model.WatermarkDefinition;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WatermarkDefinitionReadHandler extends AbstractXmlReadHandler {
  private WatermarkDefinition watermarkDefinition;

  public WatermarkDefinitionReadHandler( final WatermarkDefinition watermarkDefinition ) {

    this.watermarkDefinition = watermarkDefinition;
  }


  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String sourceAttr = attrs.getValue( getUri(), "source" );
    watermarkDefinition.setSource( sourceAttr );
    final String xAttr = attrs.getValue( getUri(), "x" );
    watermarkDefinition.setX( Length.parseLength( xAttr ) );
    final String yAttr = attrs.getValue( getUri(), "y" );
    watermarkDefinition.setY( Length.parseLength( yAttr ) );
    final String widthAttr = attrs.getValue( getUri(), "width" );
    watermarkDefinition.setWidth( Length.parseLength( widthAttr ) );
    final String heightAttr = attrs.getValue( getUri(), "height" );
    watermarkDefinition.setHeight( Length.parseLength( heightAttr ) );
    final String scaleAttr = attrs.getValue( getUri(), "scale" );
    watermarkDefinition.setScale( ParserUtil.parseBoolean( scaleAttr, getLocator() ) );
    final String keepARAttr = attrs.getValue( getUri(), "keep-aspect-ratio" );
    watermarkDefinition.setKeepAspectRatio( ParserUtil.parseBoolean( keepARAttr, getLocator() ) );
    final String visibleAttr = attrs.getValue( getUri(), "visible" );
    if ( visibleAttr != null ) {
      watermarkDefinition.setVisible( "true".equals( visibleAttr ) );
    }
  }

  public Object getObject() throws SAXException {
    return watermarkDefinition;
  }
}
