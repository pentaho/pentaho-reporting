/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditReportConfigUndoEntry implements UndoEntry {
  private HashMap oldConfig;
  private HashMap newConfig;

  public EditReportConfigUndoEntry( final HashMap oldConfig, final HashMap newConfig ) {
    this.oldConfig = oldConfig;
    this.newConfig = newConfig;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final HierarchicalConfiguration configuration =
      (HierarchicalConfiguration) renderContext.getContextRoot().getConfiguration();

    final Iterator newEntries = newConfig.entrySet().iterator();
    while ( newEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) newEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, null );
    }

    final Iterator oldEntries = oldConfig.entrySet().iterator();
    while ( oldEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) oldEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, (String) entry.getValue() );
    }
    renderContext.getContextRoot().notifyNodePropertiesChanged();
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final HierarchicalConfiguration configuration =
      (HierarchicalConfiguration) renderContext.getContextRoot().getConfiguration();

    final Iterator newEntries = oldConfig.entrySet().iterator();
    while ( newEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) newEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, null );
    }

    final Iterator oldEntries = newConfig.entrySet().iterator();
    while ( oldEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) oldEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, (String) entry.getValue() );
    }
    renderContext.getContextRoot().notifyNodePropertiesChanged();
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
