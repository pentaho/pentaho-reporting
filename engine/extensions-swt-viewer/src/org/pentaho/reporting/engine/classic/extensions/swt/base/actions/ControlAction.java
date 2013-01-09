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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.base.actions;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * ControlAction.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewPane;

/**
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class ControlAction extends Action
{
  private class EnableChangeListener implements PropertyChangeListener
  {
    protected EnableChangeListener()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      setEnabled(actionPlugin.isEnabled());
    }
  }

  private ControlActionPlugin actionPlugin;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string and
   * default icon.
   */
  public ControlAction(final ControlActionPlugin actionPlugin,
      final PreviewPane previewPane)
  {
    if (actionPlugin == null)
    {
      throw new NullPointerException();
    }
    if (previewPane == null)
    {
      throw new NullPointerException();
    }

    this.actionPlugin = actionPlugin;
    this.actionPlugin.addPropertyChangeListener(
        "enabled", new EnableChangeListener()); //$NON-NLS-1$
    this.previewPane = previewPane;

    setText(actionPlugin.getDisplayName());
    setToolTipText(actionPlugin.getShortDescription());
    setImageDescriptor(actionPlugin.getImageDescriptor());
    setEnabled(actionPlugin.isEnabled());
  }

  /**
   * Invoked when an action occurs.
   */
  public void run()
  {
    actionPlugin.configure(previewPane);
  }
}
