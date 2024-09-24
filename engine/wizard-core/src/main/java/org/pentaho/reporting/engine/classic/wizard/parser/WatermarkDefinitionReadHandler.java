/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
