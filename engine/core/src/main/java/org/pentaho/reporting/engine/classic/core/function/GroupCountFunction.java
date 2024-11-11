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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * A report function that counts groups in a report. If a null-groupname is given, all groups are counted.
 * <p/>
 * The group to be counted can be defined using the property "group". An optional container group can be defined using
 * the property "parent-group". When the group start event of that group is encountered, the counter will be reset to
 * '0'.
 * <p/>
 * If the group property is not set, all group starts get counted.
 *
 * @author David Gilbert
 */
public class GroupCountFunction extends AbstractFunction {
  /**
   * The name of the group that should be counted. This can be set to null to compute the count for all sub-groups of
   * the parent-group.
   */
  private String group;
  /**
   * The name of the group on which to reset the count. This can be set to null to count the sub-groups of the whole
   * report.
   */
  private String parentGroup;
  /**
   * The number of groups.
   */
  private int count;

  /**
   * Default constructor.
   */
  public GroupCountFunction() {
  }

  /**
   * Constructs a report function for counting groups.
   *
   * @param name
   *          The function name.
   * @param group
   *          The group name.
   * @throws NullPointerException
   *           if the given name is null
   */
  public GroupCountFunction( final String name, final String group ) {
    setName( name );
    setGroup( group );
  }

  /**
   * Returns the name of the group on which to reset the counter.
   *
   * @return the name of the group or null, if all groups are counted
   */
  public String getParentGroup() {
    return parentGroup;
  }

  /**
   * defines the name of the group on which to reset the counter. If the name is null, all groups are counted.
   *
   * @param group
   *          the name of the group to be counted.
   */
  public void setParentGroup( final String group ) {
    this.parentGroup = group;
  }

  /**
   * Returns the name of the group to be counted.
   *
   * @return the name of the group or null, if all groups are counted
   */
  public String getGroup() {
    return group;
  }

  /**
   * defines the name of the group to be counted. If the name is null, all groups are counted.
   *
   * @param group
   *          the name of the group to be counted.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Receives notification that a new report is about to start. Resets the count.
   *
   * @param event
   *          the current report event received.
   */
  public void reportInitialized( final ReportEvent event ) {
    setCount( 0 );
  }

  /**
   * Receives notification that a new group is about to start. Increases the count if all groups are counted or the name
   * defines the current group.
   *
   * @param event
   *          the current report event received.
   */
  public void groupStarted( final ReportEvent event ) {
    final Group group = FunctionUtilities.getCurrentGroup( event );

    if ( group.matches( getParentGroup() ) ) {
      setCount( 0 );
    }

    if ( getGroup() == null ) {
      // count all groups...
      setCount( getCount() + 1 );
    } else if ( group.matches( getGroup() ) ) {
      setCount( getCount() + 1 );
    }
  }

  /**
   * Returns the current group count value.
   *
   * @return the curernt group count.
   */
  protected int getCount() {
    return count;
  }

  /**
   * Defines the current group count value.
   *
   * @param count
   *          the curernt group count.
   */
  protected void setCount( final int count ) {
    this.count = count;
  }

  /**
   * Returns the number of groups processed so far (including the current group).
   *
   * @return the number of groups processed as java.lang.Integer.
   */
  public Object getValue() {
    return getCount();
  }
}
