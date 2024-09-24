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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.action;

import java.awt.Component;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * A base class for all file operations. This implementation provides all methods to let the user select a file.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractFileSelectionAction extends AbstractAction {
  /**
   * The FileChooser that is used to perform the selection.
   */
  private JFileChooser fileChooser;
  /**
   * The (optional) parent component.
   */
  private Component parent;

  /**
   * Creates a new FileSelectionAction with the given optional parent component as parent for the file chooser dialog.
   *
   * @param parent
   *          the parent
   */
  protected AbstractFileSelectionAction( final Component parent ) {
    this.parent = parent;
  }

  /**
   * Returns the file extension that should be used for the operation.
   *
   * @return the file extension.
   */
  protected abstract String getFileExtension();

  /**
   * Returns a descriptive text describing the file extension.
   *
   * @return the file description.
   */
  protected abstract String getFileDescription();

  /**
   * Returns the working directory that should be used when initializing the FileChooser.
   *
   * @return the working directory.
   */
  protected File getCurrentDirectory() {
    return new File( "." );
  }

  /**
   * Selects a file to use as target for the operation.
   *
   * @param selectedFile
   *          the selected file.
   * @param dialogType
   *          the dialog type.
   * @param appendExtension
   *          true, if the file extension should be added if necessary, false if the unmodified filename should be used.
   * @return the selected and approved file or null, if the user canceled the operation
   */
  protected File performSelectFile( final File selectedFile, final int dialogType, final boolean appendExtension ) {
    if ( this.fileChooser == null ) {
      this.fileChooser = createFileChooser();
    }

    this.fileChooser.setSelectedFile( selectedFile );
    this.fileChooser.setDialogType( dialogType );
    final int option = this.fileChooser.showDialog( this.parent, null );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = this.fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();
      if ( StringUtils.endsWithIgnoreCase( selFileName, getFileExtension() ) == false ) {
        selFileName = selFileName + getFileExtension();
      }
      return new File( selFileName );
    }
    return null;
  }

  /**
   * Creates the file chooser.
   *
   * @return the initialized file chooser.
   */
  protected JFileChooser createFileChooser() {
    final JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter( new FilesystemFilter( getFileExtension(), getFileDescription() ) );
    fc.setMultiSelectionEnabled( false );
    fc.setCurrentDirectory( getCurrentDirectory() );
    return fc;
  }

}
