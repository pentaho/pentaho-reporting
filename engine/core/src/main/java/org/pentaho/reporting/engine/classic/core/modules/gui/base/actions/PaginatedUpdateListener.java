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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Creation-Date: 15.08.2007, 16:12:34
 *
 * @author Thomas Morgner
 */
public class PaginatedUpdateListener implements PropertyChangeListener {
  private AbstractActionPlugin actionPlugin;

  public PaginatedUpdateListener( final AbstractActionPlugin actionPlugin ) {
    if ( actionPlugin == null ) {
      throw new NullPointerException();
    }
    this.actionPlugin = actionPlugin;
  }

  public void propertyChange( final PropertyChangeEvent evt ) {
    if ( PreviewPane.PAGINATED_PROPERTY.equals( evt.getPropertyName() ) == false ) {
      return;
    }

    actionPlugin.setEnabled( Boolean.TRUE.equals( evt.getNewValue() ) );
  }
}
