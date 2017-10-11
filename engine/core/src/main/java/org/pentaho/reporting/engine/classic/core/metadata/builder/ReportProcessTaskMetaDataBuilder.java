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

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportProcessTaskMetaDataBuilder extends MetaDataBuilder<ReportProcessTaskMetaDataBuilder> {
  private Class<? extends ReportProcessTask> implementation;
  private ArrayList<String> aliases;

  public ReportProcessTaskMetaDataBuilder() {
    aliases = new ArrayList<String>();
  }

  public ReportProcessTaskMetaDataBuilder implementation( final Class<? extends ReportProcessTask> implementation ) {
    this.implementation = implementation;
    return self();
  }

  public ReportProcessTaskMetaDataBuilder aliases( final Iterable<String> aliases ) {
    for ( final String alias : aliases ) {
      this.aliases.add( alias );
    }
    return self();
  }

  public ReportProcessTaskMetaDataBuilder alias( final String alias ) {
    this.aliases.add( alias );
    return self();
  }

  public ReportProcessTaskMetaDataBuilder aliases( final ArrayList<String> aliases ) {
    this.aliases = aliases;
    return self();
  }

  public Class<? extends ReportProcessTask> getImplementation() {
    return implementation;
  }

  public List<String> getAliases() {
    return Collections.unmodifiableList( (List<String>) aliases.clone() );
  }

  protected ReportProcessTaskMetaDataBuilder self() {
    return this;
  }
}
