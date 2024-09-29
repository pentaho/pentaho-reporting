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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.util;

import org.olap4j.metadata.Member;

import java.util.Collection;
import java.util.LinkedHashMap;

public class ResultSetOrderMemberAddingStrategy implements MemberAddingStrategy {
  private LinkedHashMap<String, Member> backend;

  public ResultSetOrderMemberAddingStrategy() {
    backend = new LinkedHashMap<String, Member>();
  }

  public void add( final Member m ) {
    backend.put( m.getLevel().getUniqueName(), m );
  }

  public Collection<Member> values() {
    return backend.values();
  }
}
