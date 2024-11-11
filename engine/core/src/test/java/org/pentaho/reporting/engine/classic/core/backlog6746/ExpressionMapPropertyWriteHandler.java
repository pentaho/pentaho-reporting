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


package org.pentaho.reporting.engine.classic.core.backlog6746;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleExpressionPropertyWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.ExpressionWriterUtility;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.util.Map;

public class ExpressionMapPropertyWriteHandler implements BundleExpressionPropertyWriteHandler {
  private WriteableDocumentBundle bundle;
  private BundleWriterState state;

  @Override
  public void initBundleContext( WriteableDocumentBundle bundle, BundleWriterState state ) {
    this.bundle = bundle;
    this.state = state;
  }

  @Override
  public void writeExpressionParameter( XmlWriter writer,
                                        BeanUtility beanUtility,
                                        String propertyName,
                                        String namespaceUri ) throws IOException, BeanException {

    final Object property = beanUtility.getProperty( propertyName );
    if (property == null) {
      return;
    }
    if ((property instanceof Map) == false) {
      throw new BeanException( "Unexpected property type" );
    }

    Map<String, Expression> e = (Map<String, Expression>) property;
    if (e.isEmpty()) {
      return;
    }

    final Class propertyType = beanUtility.getPropertyType( propertyName );
    final AttributeList attList = new AttributeList();
    attList.setAttribute( namespaceUri, "name", propertyName );
    if ( BeanUtility.isSameType( propertyType, property.getClass() ) == false ) {
      attList.setAttribute( namespaceUri, "class", property.getClass().getName() );
    }
    writer.writeTag( namespaceUri, "property", attList, XmlWriterSupport.OPEN );

    for ( Map.Entry<String, Expression> entry : e.entrySet() ) {
      final Expression instance = entry.getValue().getInstance();
      instance.setName( entry.getKey() );
      try {
        ExpressionWriterUtility.writeExpression( bundle, state, instance, writer, namespaceUri, "expression" );
      } catch ( BundleWriterException ex ) {
        throw new IOException( "Unable to write nested expression", ex );
      }
    }

    writer.writeCloseTag();
  }
}
