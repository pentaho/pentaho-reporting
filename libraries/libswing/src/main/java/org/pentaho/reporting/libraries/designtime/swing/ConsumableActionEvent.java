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

package org.pentaho.reporting.libraries.designtime.swing;

import java.awt.event.ActionEvent;

public class ConsumableActionEvent extends ActionEvent {
  private boolean consumed;

  /**
   * Constructs an <code>ActionEvent</code> object.
   * <p/>
   * Note that passing in an invalid <code>id</code> results in unspecified behavior. This method throws an
   * <code>IllegalArgumentException</code> if <code>source</code> is <code>null</code>. A <code>null</code>
   * <code>command</code> string is legal, but not recommended.
   *
   * @param source  the object that originated the event
   * @param id      an integer that identifies the event
   * @param command a string that may specify a command (possibly one of several) associated with the event
   * @throws IllegalArgumentException if <code>source</code> is null
   */
  public ConsumableActionEvent( final Object source, final int id, final String command ) {
    super( source, id, command );
  }

  /**
   * Constructs an <code>ActionEvent</code> object with modifier keys.
   * <p/>
   * Note that passing in an invalid <code>id</code> results in unspecified behavior. This method throws an
   * <code>IllegalArgumentException</code> if <code>source</code> is <code>null</code>. A <code>null</code>
   * <code>command</code> string is legal, but not recommended.
   *
   * @param source    the object that originated the event
   * @param id        an integer that identifies the event
   * @param command   a string that may specify a command (possibly one of several) associated with the event
   * @param modifiers the modifier keys held down during this action
   * @throws IllegalArgumentException if <code>source</code> is null
   */
  public ConsumableActionEvent( final Object source, final int id, final String command, final int modifiers ) {
    super( source, id, command, modifiers );
  }

  /**
   * Constructs an <code>ActionEvent</code> object with the specified modifier keys and timestamp.
   * <p/>
   * Note that passing in an invalid <code>id</code> results in unspecified behavior. This method throws an
   * <code>IllegalArgumentException</code> if <code>source</code> is <code>null</code>. A <code>null</code>
   * <code>command</code> string is legal, but not recommended.
   *
   * @param source    the object that originated the event
   * @param id        an integer that identifies the event
   * @param command   a string that may specify a command (possibly one of several) associated with the event
   * @param when      the time the event occurred
   * @param modifiers the modifier keys held down during this action
   * @throws IllegalArgumentException if <code>source</code> is null
   * @since 1.4
   */
  public ConsumableActionEvent( final Object source,
                                final int id, final String command, final long when, final int modifiers ) {
    super( source, id, command, when, modifiers );
  }

  /**
   * Returns whether this event has been consumed.
   */
  public boolean isConsumed() {
    return consumed;
  }

  /**
   * Consumes this event, if this event can be consumed. Only low-level, system events can be consumed
   */
  public void consume() {
    consumed = true;
  }
}
