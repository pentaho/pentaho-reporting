/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.states;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import java.util.ArrayList;

public class GroupSizeRecorderTest extends TestCase {
  public GroupSizeRecorderTest() {
  }

  public GroupSizeRecorderTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testNotCrashing() {
    final DefaultGroupSizeRecorder g = new DefaultGroupSizeRecorder();
    g.enterGroup();
    {
      g.enterGroup();
      {
        g.enterGroup();
        {
          g.enterGroup();
          {
            // items
            g.enterItems();
            g.advanceItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
          g.enterGroup();
          {
            // items
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
          g.enterGroup();
          {
            // items irregular
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
          g.enterGroup();
          {
            // items irregular
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
        }
        g.leaveGroup();
        g.enterGroup();
        {
          g.enterGroup();
          {
            // items
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
          g.enterGroup();
          {
            // items
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
          g.enterGroup();
          {
            // items irregular
            g.enterItems();
            g.advanceItems();
            g.leaveItems();
          }
          g.leaveGroup();
        }
        g.leaveGroup();
      }
      g.leaveGroup();
    }
    g.leaveGroup();
    final int[] groupCounts = g.getGroupCounts();
    final ArrayList l = new ArrayList();
    for ( int i = 0; i < groupCounts.length; i++ ) {
      l.add( groupCounts[i] );
    }

    System.out.println( l );
    System.out.println( g.getCurrentGroupIndex() );
  }

  public void testFail() {
    final GroupSizeRecorder g = new DefaultGroupSizeRecorder();
    g.clone();
    g.clone();
    g.clone();
    g.enterGroup();
    g.clone();
    g.enterItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.advanceItems();
    g.clone();
    g.leaveItems();
    g.clone();
    g.clone();
    g.leaveGroup();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.reset();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.clone();
    g.enterGroup();
    g.clone();
    g.enterItems();
  }

}
