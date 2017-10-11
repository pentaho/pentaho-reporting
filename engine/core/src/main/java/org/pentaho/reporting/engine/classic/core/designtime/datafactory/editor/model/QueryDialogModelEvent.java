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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model;

import java.util.EventObject;

public class QueryDialogModelEvent<T> extends EventObject {
  private final QueryDialogModel<T> eventSource;
  private final int newIndex;
  private final Query<T> newQuery;
  private final int oldIndex;
  private final Query<T> oldQuery;

  public QueryDialogModelEvent( final QueryDialogModel<T> source ) {
    this( source, -1, null );
  }

  public QueryDialogModelEvent( final QueryDialogModel<T> source, final int index, final Query<T> query ) {
    this( source, index, query, index, query );
  }

  public QueryDialogModelEvent( final QueryDialogModel<T> source, final int newIndex, final Query<T> newQuery,
      final int oldIndex, final Query<T> oldQuery ) {
    super( source );
    this.eventSource = source;
    this.newIndex = newIndex;
    this.newQuery = newQuery;
    this.oldIndex = oldIndex;
    this.oldQuery = oldQuery;
  }

  public QueryDialogModel<T> getEventSource() {
    return eventSource;
  }

  public int getNewIndex() {
    return newIndex;
  }

  public Query<T> getNewQuery() {
    return newQuery;
  }

  public int getOldIndex() {
    return oldIndex;
  }

  public Query<T> getOldQuery() {
    return oldQuery;
  }
}
