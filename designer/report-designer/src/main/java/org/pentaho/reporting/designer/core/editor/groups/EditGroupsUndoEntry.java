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

package org.pentaho.reporting.designer.core.editor.groups;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditGroupsUndoEntry implements UndoEntry {
  private GroupDataEntry[] oldGroupData;
  private GroupDataEntry[] newGroupData;

  public EditGroupsUndoEntry( final GroupDataEntry[] oldGroupData,
                              final GroupDataEntry[] newGroupData ) {
    this.oldGroupData = oldGroupData;
    this.newGroupData = newGroupData;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    applyGroupData( renderContext.getReportDefinition(), oldGroupData );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    applyGroupData( renderContext.getReportDefinition(), newGroupData );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }

  public static GroupDataEntry[] buildGroupData( final AbstractReportDefinition abstractReportDefinition ) {
    Group group = abstractReportDefinition.getRootGroup();
    final ArrayList<GroupDataEntry> data = new ArrayList();
    while ( group instanceof RelationalGroup ) {
      final RelationalGroup rgroup = (RelationalGroup) group;
      data.add( new GroupDataEntry( group.getObjectID(), group.getName(), rgroup.getFieldsArray() ) );

      final GroupBody body = rgroup.getBody();
      if ( body instanceof SubGroupBody ) {
        final SubGroupBody subGroupBody = (SubGroupBody) body;
        group = subGroupBody.getGroup();
      } else {
        group = null;
      }
    }

    return data.toArray( new GroupDataEntry[ data.size() ] );
  }

  public static void applyGroupData( final AbstractReportDefinition abstractReportDefinition,
                                     final GroupDataEntry[] newEntries ) {
    final HashMap<InstanceID, RelationalGroup> groups = new HashMap();
    RelationalGroup innermostGroup = null;

    Group group = abstractReportDefinition.getRootGroup();
    while ( group instanceof RelationalGroup ) {
      final RelationalGroup rgroup = (RelationalGroup) group;
      innermostGroup = rgroup;
      groups.put( group.getObjectID(), rgroup );

      final GroupBody body = rgroup.getBody();
      if ( body instanceof SubGroupBody ) {
        final SubGroupBody subGroupBody = (SubGroupBody) body;
        group = subGroupBody.getGroup();
      } else {
        group = null;
      }
    }

    final RelationalGroup[] resultGroups = new RelationalGroup[ newEntries.length ];
    for ( int i = 0; i < newEntries.length; i++ ) {
      final GroupDataEntry o = newEntries[ i ];
      final RelationalGroup sourceGroup = groups.get( o.getInstanceID() );
      if ( sourceGroup == null ) {
        resultGroups[ i ] = new RelationalGroup();
      } else {
        resultGroups[ i ] = sourceGroup;
      }

      resultGroups[ i ].setName( o.getName() );
      resultGroups[ i ].setFieldsArray( o.getFields() );
    }

    final GroupBody innermostBody;
    if ( innermostGroup == null ) {
      innermostBody = new SubGroupBody( abstractReportDefinition.getRootGroup() );
    } else {
      innermostBody = innermostGroup.getBody();
    }

    if ( resultGroups.length > 0 ) {
      for ( int i = 0; i < resultGroups.length; i++ ) {
        final RelationalGroup resultGroup = resultGroups[ i ];
        if ( i == 0 ) {
          abstractReportDefinition.setRootGroup( resultGroup );
        } else {
          final RelationalGroup prevGroup = resultGroups[ i - 1 ];
          final GroupBody body = prevGroup.getBody();
          if ( body instanceof SubGroupBody ) {
            final SubGroupBody sgb = (SubGroupBody) body;
            sgb.setGroup( resultGroup );
          } else {
            final SubGroupBody sgb = new SubGroupBody();
            sgb.setGroup( resultGroup );
            prevGroup.setBody( sgb );
          }
        }
      }

      resultGroups[ resultGroups.length - 1 ].setBody( innermostBody );
    } else {

      // all groups had been removed ..
      final RelationalGroup relationalGroup = new RelationalGroup();
      relationalGroup.setBody( innermostBody );
      abstractReportDefinition.setRootGroup( relationalGroup );
    }
  }
}
