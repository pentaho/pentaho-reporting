/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public interface ExpressionPropertyWriteHandler {
  void writeExpressionParameter( final XmlWriter writer,
                                 final BeanUtility beanUtility,
                                 final String propertyName,
                                 final String namespaceUri ) throws IOException, BeanException;

}
