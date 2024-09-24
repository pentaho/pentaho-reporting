/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
