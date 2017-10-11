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
