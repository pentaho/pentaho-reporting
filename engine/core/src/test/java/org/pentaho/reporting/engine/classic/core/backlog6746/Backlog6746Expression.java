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

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Backlog6746Expression extends AbstractExpression {
  private LinkedHashMap<String,Expression> map;

  public Backlog6746Expression() {
    map = new LinkedHashMap<>();
  }

  /// we never ever give out a direct reference to our private collections.
  /// What's mine is mine and so on ..
  public Map<String,Expression> getExpressionMap() {
    return new LinkedHashMap<>( map );
  }

  /// and we never let others redefine out own collection instances either.
  public void setExpressionMap(Map<String,Expression> values) {
    map.clear();
    map.putAll( values );
  }

  public void addExpression(String property, Expression e){
    map.put(property, e);
  }

  public void removeExpression(String property) {
    map.remove( property );
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    // called during the report processing. This preserves state that may change while a report is running.
    // does nothing. Note that we do not clone the map
    return super.clone();
  }

  @Override
  public Expression getInstance() {
    // called at design-time and when we clone whole reports. It is important that the new instance has
    // absolutely no shared state with the old instance. So we make a deep clone here ..
    final Backlog6746Expression instance = (Backlog6746Expression) super.getInstance();
    instance.map = (LinkedHashMap<String, Expression>) map.clone();
    for ( Map.Entry<String, Expression> entry : map.entrySet() ) {
      entry.setValue( entry.getValue().getInstance() );
    }
    return instance;
  }

  @Override
  public Object getValue() {
    return map;
  }
}
