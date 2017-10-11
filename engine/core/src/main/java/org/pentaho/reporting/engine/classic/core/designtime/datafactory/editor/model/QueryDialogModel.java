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

public interface QueryDialogModel<T> extends Iterable<Query<T>> {
  QueryDialogComboBoxModel<T> getQueries();

  boolean isQuerySelected();

  void setSelectedQuery( Query<T> query );

  Query<T> getSelectedQuery();

  void addQuery( Query<T> query );

  void removeQuery( Query<T> query );

  void updateQuery( int index, Query<T> query );

  int getQueryCount();

  Query<T> getQuery( int index );

  void addQueryDialogModelListener( QueryDialogModelListener<T> listener );

  void removeQueryDialogModelListener( QueryDialogModelListener<T> listener );

  void updateSelectedQuery( Query<T> newQuery );

  void setGlobalScripting( final String lang, final String script );

  String getGlobalScriptLanguage();

  String getGlobalScript();

  void clear();
}
