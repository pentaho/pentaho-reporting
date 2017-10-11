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

package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionMetaDataBuilder extends MetaDataBuilder<ExpressionMetaDataBuilder> {

  private Class<? extends Expression> impl;
  private Class<?> resultType;

  private LinkedHashMap<String, ExpressionPropertyMetaData> properties;
  private int layoutComputation;

  public ExpressionMetaDataBuilder() {
    this.properties = new LinkedHashMap<String, ExpressionPropertyMetaData>();
    this.resultType = Object.class;
    this.layoutComputation = DefaultExpressionMetaData.NO_LAYOUT_PROCESSOR;
  }

  protected ExpressionMetaDataBuilder self() {
    return this;
  }

  public ExpressionMetaDataBuilder impl( final Class<? extends Expression> expressionClass ) {
    this.impl = expressionClass;
    return self();
  }

  public String getName() {
    if ( impl == null ) {
      return null;
    }
    return impl.getName();
  }

  public ExpressionMetaDataBuilder resultType( final Class<?> resultType ) {
    this.resultType = resultType;
    return self();
  }

  public ExpressionMetaDataBuilder properties( final Map<String, ExpressionPropertyMetaData> properties ) {
    this.properties.putAll( properties );
    return self();
  }

  public ExpressionMetaDataBuilder property( final ExpressionPropertyMetaData p ) {
    this.properties.put( p.getName(), p );
    return self();
  }

  public ExpressionMetaDataBuilder layoutComputation( final int layoutComputation ) {
    this.layoutComputation = layoutComputation;
    return self();
  }

  public Class<? extends Expression> getImpl() {
    return impl;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public LinkedHashMap<String, ExpressionPropertyMetaData> getProperties() {
    return properties;
  }

  public int getLayoutComputation() {
    return layoutComputation;
  }
}
