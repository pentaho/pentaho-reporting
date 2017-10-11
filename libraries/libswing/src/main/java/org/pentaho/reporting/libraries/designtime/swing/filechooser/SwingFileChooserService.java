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

package org.pentaho.reporting.libraries.designtime.swing.filechooser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class SwingFileChooserService implements CommonFileChooser {
  private static class FileSelectionTask implements Runnable {
    private FileFilter[] filters;
    private Component parent;
    private int mode;
    private int retval;
    private File[] selectedFiles;
    private boolean allowMultiSelection;
    private File currentDirectory;

    private FileSelectionTask( final Component parent,
                               final int mode,
                               final FileFilter[] filters,
                               final File[] selectedFiles,
                               final File currentDirectory,
                               final boolean allowMultiSelection ) {
      this.parent = parent;
      this.mode = mode;
      this.filters = filters;
      this.selectedFiles = selectedFiles;
      this.currentDirectory = currentDirectory;
      this.allowMultiSelection = allowMultiSelection;
    }

    public int getRetval() {
      return retval;
    }

    public File[] getSelectedFiles() {
      return selectedFiles;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      if ( fileChooser == null ) {
        fileChooser = new JFileChooser();
      }

      fileChooser.setMultiSelectionEnabled( allowMultiSelection );
      fileChooser.setCurrentDirectory( currentDirectory );
      fileChooser.setSelectedFiles( selectedFiles );
      fileChooser.updateUI();
      fileChooser.resetChoosableFileFilters();
      for ( int i = 0; i < filters.length; i++ ) {
        fileChooser.addChoosableFileFilter( filters[ i ] );
      }

      if ( mode == JFileChooser.OPEN_DIALOG ) {
        retval = fileChooser.showOpenDialog( parent );
      } else {
        retval = fileChooser.showSaveDialog( parent );
      }

      if ( retval == JFileChooser.APPROVE_OPTION ) {
        if ( fileChooser.isMultiSelectionEnabled() ) {
          selectedFiles = fileChooser.getSelectedFiles();
        } else {
          selectedFiles = new File[] { fileChooser.getSelectedFile() };
        }
      } else {
        selectedFiles = EMPTY_FILES;
      }
    }
  }


  protected static final File[] EMPTY_FILES = new File[ 0 ];
  protected static final FileFilter[] EMPTY_FILEFILTER = new FileFilter[ 0 ];
  protected static final Object lock = new Object();

  private static JFileChooser fileChooser;
  private String fileType;
  private File[] selectedFiles;
  private boolean allowMultiSelection;
  private FileFilter[] filters;

  public SwingFileChooserService( final String fileType ) {
    this.fileType = fileType;
    this.selectedFiles = EMPTY_FILES;
    this.filters = EMPTY_FILEFILTER;
  }

  public String getFileType() {
    return fileType;
  }

  public File[] getSelectedFiles() {
    return selectedFiles.clone();
  }

  public File getSelectedFile() {
    if ( selectedFiles.length == 0 ) {
      return null;
    }
    return selectedFiles[ 0 ];
  }

  public void setSelectedFile( final File selectedFile ) {
    if ( selectedFile == null ) {
      this.selectedFiles = EMPTY_FILES;
    } else {
      this.selectedFiles = new File[] { selectedFile };
    }
  }

  public void setSelectedFiles( final File[] selectedFiles ) {
    this.selectedFiles = selectedFiles.clone();
  }

  public boolean isAllowMultiSelection() {
    return allowMultiSelection;
  }

  public void setAllowMultiSelection( final boolean allowMultiSelection ) {
    this.allowMultiSelection = allowMultiSelection;
  }

  public FileFilter[] getFilters() {
    return filters.clone();
  }

  public void setFilters( final FileFilter[] filters ) {
    this.filters = filters.clone();
  }

  public boolean showDialog( final Component parent, final int mode ) {
    File currentDirectory = null;
    if ( selectedFiles.length == 0 ) {
      if ( FileChooserService.getInstance().isStoreLocations( getFileType() ) ) {
        final File lastLocation = FileChooserService.getInstance().getLastLocation( getFileType() );
        if ( lastLocation != null ) {
          if ( lastLocation.isDirectory() ) {
            currentDirectory = lastLocation;
            setSelectedFile( null );
          } else {
            currentDirectory = lastLocation.getParentFile();
            setSelectedFile( lastLocation );
          }
        }
      } else {
        final File staticLocation = FileChooserService.getInstance().getStaticLocation( getFileType() );
        if ( staticLocation != null ) {
          if ( staticLocation.isDirectory() ) {
            currentDirectory = staticLocation;
            setSelectedFile( null );
          } else {
            currentDirectory = staticLocation.getParentFile();
            setSelectedFile( staticLocation );
          }
        }
      }
    }

    final FileSelectionTask task = new FileSelectionTask
      ( parent, mode, getFilters(), getSelectedFiles(), currentDirectory, isAllowMultiSelection() );

    if ( SwingUtilities.isEventDispatchThread() ) {
      task.run();
    } else {
      try {
        SwingUtilities.invokeAndWait( task );
      } catch ( Exception e ) {
        return false;
      }
    }

    if ( task.getRetval() == JFileChooser.APPROVE_OPTION ) {
      selectedFiles = task.getSelectedFiles();
      final File selectedFile = getSelectedFile();
      if ( selectedFile != null && FileChooserService.getInstance().isStoreLocations( getFileType() ) ) {
        FileChooserService.getInstance().setLastLocation( getFileType(), selectedFile );
      }

      return true;
    } else {
      return false;
    }
  }
}
