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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class DefaultExpressionPropertyWriteHandler implements ExpressionPropertyWriteHandler {

  public void writeExpressionParameter( final XmlWriter writer,
                                        final BeanUtility beanUtility,
                                        final String propertyName,
                                        final String namespaceUri ) throws IOException, BeanException {
    // filter some of the standard properties. These are system-properties
    // and are set elsewhere

    final Object property = beanUtility.getProperty( propertyName );
    final Class propertyType = beanUtility.getPropertyType( propertyName );
    final String value = beanUtility.getPropertyAsString( propertyName );
    if ( value != null && property != null ) {
      final AttributeList attList = new AttributeList();
      attList.setAttribute( namespaceUri, "name", propertyName );
      if ( BeanUtility.isSameType( propertyType, property.getClass() ) == false ) {
        attList.setAttribute( namespaceUri, "class", property.getClass().getName() );
      }
      writer.writeTag( namespaceUri, "property", attList, XmlWriterSupport.OPEN );
      writer.writeTextNormalized( value, false );
      writer.writeCloseTag();
    }
  }

}
