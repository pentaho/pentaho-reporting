/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creates anchor objects for the current group. The anchors generated consist of the group's name and the current group
 * count.
 * <p/>
 * To use the CreateGroupAnchorsFunction set the group's name as the function's group-property value. Next, add a
 * AnchorElement to where the anchor should be generated (usually either the group-header or footer) and give the
 * function's name as fieldname in the anchor-field.
 *
 * @author Thomas Morgner
 * @deprecated It is easier to create anchors using a Style-expression. The Anchor-Field has been deprecated now.
 */
public class CreateGroupAnchorsFunction extends AbstractFunction {
  /**
   * The name of the group for which anchors should be created.
   */
  private String group;
  /**
   * A prefix for the anchor name.
   */
  private String anchorPrefix;
  /**
   * A temporary variable holding the last anchor created by this function.
   */
  private String anchor;
  /**
   * A counter to create unique anchor names.
   */
  private int count;

  /**
   * Default Constructor. Does nothing.
   */
  public CreateGroupAnchorsFunction() {
    anchorPrefix = "anchor";
  }

  /**
   * Returns the prefix for the generated anchor.
   *
   * @return the anchor prefix.
   */
  public String getAnchorPrefix() {
    return anchorPrefix;
  }

  /**
   * Defines the prefix for the generated anchor.
   *
   * @param anchorPrefix
   *          the prefix for the anchor.
   */
  public void setAnchorPrefix( final String anchorPrefix ) {
    if ( anchorPrefix == null ) {
      throw new NullPointerException( "The Anchor-Prefix must not be null." );
    }
    this.anchorPrefix = anchorPrefix;
  }

  /**
   * Returns the name of the group for which an anchor should be generated.
   *
   * @return the name of the group.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Defines the name of the group for which an anchor should be generated.
   *
   * @param group
   *          the name of the group.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    count = 0;

    final StringBuilder targetBuffer = new StringBuilder();
    final String prefix = getAnchorPrefix();
    targetBuffer.append( prefix );
    targetBuffer.append( getGroup() );
    targetBuffer.append( "%3D" );
    targetBuffer.append( count );
    anchor = targetBuffer.toString();
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) == false ) {
      return;
    }

    final StringBuilder targetBuffer = new StringBuilder();
    final String prefix = getAnchorPrefix();
    targetBuffer.append( prefix );
    targetBuffer.append( getGroup() );
    targetBuffer.append( "%3D" );
    targetBuffer.append( count );
    anchor = targetBuffer.toString();
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return anchor;
  }
}
