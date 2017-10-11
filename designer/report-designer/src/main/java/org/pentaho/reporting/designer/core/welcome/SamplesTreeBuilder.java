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

package org.pentaho.reporting.designer.core.welcome;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.settings.SettingsUtil;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SamplesTreeBuilder {
  private static class DirectoryNode extends DefaultMutableTreeNode {
    /**
     * Creates a tree node with no parent, no children, but which allows children, and initializes it with the specified
     * user object.
     *
     * @param userObject an Object provided by the user that constitutes the node's data
     */
    private DirectoryNode( final Object userObject ) {
      super( userObject );
    }

    /**
     * Returns true if this node has no children.  To distinguish between nodes that have no children and nodes that
     * <i>cannot</i> have children (e.g. to distinguish files from empty directories), use this method in conjunction
     * with <code>getAllowsChildren</code>
     *
     * @return true if this node has no children
     * @see #getAllowsChildren
     */
    public boolean isLeaf() {
      return false;
    }
  }


  private static class DirectoryFileFilter implements FileFilter {
    private DirectoryFileFilter() {
    }

    public boolean accept( final File file ) {
      if ( file.getName().length() > 0 && file.getName().charAt( 0 ) == '.' ) {
        return false;
      }
      return file.isDirectory();
    }
  }

  /**
   * HyperLink that opens a fiel as a new report, could be combined with the RecentDocButton
   *
   * @author OEM
   */
  public static class SampleNode extends DefaultMutableTreeNode {
    private String fileName;

    public SampleNode( final String lbl, final String file ) {
      super( file );
      fileName = lbl;
    }

    public String toString() {
      return fileName;
    }
  }

  private static final Log logger = LogFactory.getLog( SamplesTreeBuilder.class );
  private static DefaultTreeModel sampleTreeModel;
  private static final DirectoryFileFilter DIRECTORY_FILTER = new DirectoryFileFilter();
  private static final FilesystemFilter REPORT_FILES_FILTER =
    new FilesystemFilter( new String[] { ".report", ".prpt" }, "", false );// NON-NLS

  private SamplesTreeBuilder() {
  }

  public static synchronized TreeModel getSampleTreeModel() {
    if ( sampleTreeModel == null ) {
      sampleTreeModel = createModel();
    }
    return sampleTreeModel;
  }

  private static DefaultTreeModel createModel() {
    final DefaultMutableTreeNode root =
      new DefaultMutableTreeNode( Messages.getString( "WelcomePane.samples" ) );// NON-NLS
    final DefaultTreeModel model = new DefaultTreeModel( root );

    try {
      final ResourceManager resourceManager = new ResourceManager();
      final HashMap cache = loadFromCache( resourceManager );

      final File installationDirectory = SettingsUtil.computeInstallationDirectory();
      if ( installationDirectory != null ) {
        final File configTemplateDir = new File( installationDirectory, "samples" );// NON-NLS
        if ( configTemplateDir.exists() && configTemplateDir.isDirectory() ) {
          processDirectory( root, configTemplateDir, cache, resourceManager );
        }
      }

      storeToCache( cache );
    } catch ( Exception e ) {
      return model;
    }
    return model;
  }

  /**
   * Creates a list of SampleReports located in the /samples directory.
   */
  private static void processDirectory( final DefaultMutableTreeNode root,
                                        final File dir,
                                        final HashMap cachedEntries,
                                        final ResourceManager resourceManager ) {
    try {
      final File[] dirs = dir.listFiles( DIRECTORY_FILTER );
      if ( dirs == null ) {
        return;
      }
      for ( final File f : dirs ) {
        final DefaultMutableTreeNode dirNode = new DirectoryNode( f.getName() );
        root.add( dirNode );
        processDirectory( dirNode, f, cachedEntries, resourceManager );
      }

      //Now add sample files
      final File[] samplesArray = dir.listFiles( REPORT_FILES_FILTER );
      if ( samplesArray == null ) {
        return;
      }

      Arrays.sort( samplesArray );
      for ( final File f : samplesArray ) {
        final SampleReport entryFromCache = (SampleReport) cachedEntries.get( f.getAbsolutePath() );
        if ( entryFromCache == null ) {
          final SampleReport tempRpt = new SampleReport( f, resourceManager );
          if ( StringUtils.isEmpty( tempRpt.getReportName() ) == false ) {
            final SampleNode sample = new SampleNode( tempRpt.getReportName(), tempRpt.getFileName() );
            root.add( sample );
          }
          cachedEntries.put( f.getAbsolutePath(), tempRpt );
        } else {
          if ( StringUtils.isEmpty( entryFromCache.getReportName() ) == false ) {
            final SampleNode sample = new SampleNode( entryFromCache.getReportName(), entryFromCache.getFileName() );
            root.add( sample );
          }
        }
      }

    } catch ( Exception se ) {
      logger.error( "Cannot access Application directory", se );// NON-NLS
    }
  }

  protected static HashMap loadFromCache( final ResourceManager resourceManager ) {

    final File location = createStorageLocation();
    if ( location == null ) {
      return new HashMap();
    }
    final File ttfCache = new File( location, "samples-cache.ser" );// NON-NLS
    try {
      final ResourceKey resourceKey = resourceManager.createKey( ttfCache );
      final ResourceData data = resourceManager.load( resourceKey );
      final InputStream stream = data.getResourceAsStream( resourceManager );

      final HashMap cachedSeenFiles;

      try {
        final ObjectInputStream oin = new ObjectInputStream( stream );
        final Object[] cache = (Object[]) oin.readObject();
        if ( cache.length != 1 ) {
          return new HashMap();
        }
        cachedSeenFiles = (HashMap) cache[ 0 ];
      } finally {
        stream.close();
      }

      // next; check the font-cache for validity. We cannot cleanly remove
      // entries from the cache once they become invalid, so we have to rebuild
      // the cache from scratch, if it is invalid.
      //
      // This should not matter that much, as font installations do not happen
      // every day.
      if ( isCacheValid( cachedSeenFiles ) ) {
        return cachedSeenFiles;
      }
    } catch ( final ClassNotFoundException cnfe ) {
      // ignore the exception.
      logger
        .debug( "Failed to restore the cache: Cache was created by a different version of ReportDesigner" );// NON-NLS
    } catch ( Exception e ) {
      logger.debug( "Non-Fatal: Failed to restore the cache. The cache will be rebuilt.", e );// NON-NLS
    }

    return new HashMap();
  }

  protected static void storeToCache( final HashMap seenFiles ) {
    final File location = createStorageLocation();
    if ( location == null ) {
      return;
    }
    location.mkdirs();
    if ( location.exists() == false || location.isDirectory() == false ) {
      return;
    }

    final File ttfCache = new File( location, "samples-cache.ser" );// NON-NLS
    try {
      final FileOutputStream fout = new FileOutputStream( ttfCache );
      try {
        final Object[] map = new Object[ 1 ];
        map[ 0 ] = seenFiles;

        final ObjectOutputStream objectOut = new ObjectOutputStream( new BufferedOutputStream( fout ) );
        objectOut.writeObject( map );
        objectOut.close();
      } finally {
        try {
          fout.close();
        } catch ( IOException e ) {
          // ignore ..
          logger.debug( "Failed to store cached samples data", e );// NON-NLS
        }
      }
    } catch ( IOException e ) {
      // should not happen
      logger.debug( "Failed to store cached samples data", e );// NON-NLS
    }
  }

  protected static File createStorageLocation() {
    final String homeDirectory = safeSystemGetProperty( "user.home", null );// NON-NLS
    if ( homeDirectory == null ) {
      return null;
    }
    final File homeFile = new File( homeDirectory );
    if ( homeFile.isDirectory() == false ) {
      return null;
    }
    return new File( homeFile, ".pentaho/caches/prd-samples" );// NON-NLS
  }


  protected static String safeSystemGetProperty( final String name,
                                                 final String defaultValue ) {
    try {
      return System.getProperty( name, defaultValue );
    } catch ( SecurityException se ) {
      return defaultValue;
    }
  }


  protected static boolean isCacheValid( final HashMap cachedSeenFiles ) {
    final Iterator iterator = cachedSeenFiles.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final String fullFileName = (String) entry.getKey();
      final SampleReport fontFileRecord = (SampleReport) entry.getValue();
      final File fontFile = new File( fullFileName );
      if ( fontFile.isFile() == false || fontFile.exists() == false ) {
        return false;
      }
      if ( fontFile.length() != fontFileRecord.getFileSize() ) {
        return false;
      }
      if ( fontFile.lastModified() != fontFileRecord.getLastAccessTime() ) {
        return false;
      }
    }
    return true;
  }

}
