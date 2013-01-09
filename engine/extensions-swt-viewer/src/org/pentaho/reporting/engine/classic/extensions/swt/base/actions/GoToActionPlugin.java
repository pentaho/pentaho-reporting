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
 * GoToActionPlugin.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.util.Locale;
import javax.swing.KeyStroke;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewPane;
import org.pentaho.reporting.engine.classic.extensions.swt.base.SWTPreviewModule;
import org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.SwtGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class GoToActionPlugin extends AbstractActionPlugin implements ControlActionPlugin
{
  private ResourceBundleSupport resources;

  public GoToActionPlugin()
  {
  }

  public boolean initialize(final SwtGuiContext context)
  {
    super.initialize(context);
    resources = new ResourceBundleSupport(context.getLocale(),
        SWTPreviewModule.BUNDLE_NAME, ObjectUtilities.getClassLoader(SWTPreviewModule.class));
    context.getEventSource().addPropertyChangeListener(
        new PaginatedUpdateListener(this));
    setEnabled(context.getEventSource().isPaginated());
    return true;
  }

  protected String getConfigurationPrefix()
  {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.go-to."; //$NON-NLS-1$
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName()
  {
    return resources.getString("action.gotopage.name"); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription()
  {
    return resources.getString("action.gotopage.description"); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey()
  {
    return resources.getKeyStroke("action.gotopage.accelerator"); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey()
  {
    return resources.getMnemonic("action.gotopage.mnemonic"); //$NON-NLS-1$
  }

  public boolean configure(final PreviewPane reportPane)
  {
    final InputDialog dlg = new IntInputDialog(reportPane.getShell(), resources
        .getString("dialog.gotopage.title"), //$NON-NLS-1$
        resources.getString("dialog.gotopage.message"), //$NON-NLS-1$
        String.valueOf(reportPane.getPageNumber()), new IntRangeValidator(1, reportPane
            .getNumberOfPages()));

    if (dlg.open() == Window.OK)
    {
      // User clicked OK; update the label with the input
      final int page = Integer.parseInt(dlg.getValue());
      if (page > 0 && page <= reportPane.getNumberOfPages())
      {
        reportPane.setPageNumber(page);
      }
    }

    return false;
  }

  private static class IntInputDialog extends InputDialog
  {
    private String previousInput;
    private String initialInput;

    private IntInputDialog(final Shell parent, final String title, final String message,
                          final String initial, final IInputValidator validator)
    {
      super(parent, title, message, initial, validator);
      initialInput = initial;
      previousInput = initial;
    }

    /**
     * Reset the input text to the previous valid value if
     * the new input is invalid.
     */
    protected void validateInput()
    {
      final String newInput = getText().getText();
      final String result = getValidator().isValid(newInput);
      if (result != null)
      {
        this.getText().setText(previousInput);
      }
      else
      {
        previousInput = newInput;
      }
    }

    /**
     * Override to return the initial value
     * user input is an empty string.
     */
    public String getValue()
    {
      String result = super.getValue();
      if (result.length() == 0)
      {
        result = initialInput;
      }
      return result;
    }
  }

  /**
   * This class validates a String. It makes sure that the String is empty or
   * represents an integer between min and max.
   */
  private static class IntRangeValidator implements IInputValidator
  {
    private int minimum;
    private int maximum;

    private IntRangeValidator(final int min, final int max)
    {
      minimum = min;
      maximum = max;
    }

    /**
     * Validates the String. Returns null for no error, or an error message
     *
     * @param newText the String to validate
     * @return String
     */
    public String isValid(final String newText)
    {
      if (newText.length() == 0)
      {
        return null; //OK
      }
      try
      {
        final int i = Integer.parseInt(newText);
        if (i < minimum || i > maximum)
        {
          return "invalid";
        }
      }
      catch (NumberFormatException x)
      {
        return "invalid";
      }
      return null;
    }
  }

  /**
   * Returns the ImageDescripter for the this action.
   *
   * @return The ImageDescripter.
   */
  public ImageDescriptor getImageDescriptor()
  {
    final Locale locale = getContext().getLocale();
    final IconTheme iconTheme = getIconTheme();
    if (iconTheme == null)
    {
      return null;
    }
    else
    {
      return iconTheme.getImageDescriptor(locale, "action.gotopage.small-icon"); //$NON-NLS-1$
    }
  }
}
