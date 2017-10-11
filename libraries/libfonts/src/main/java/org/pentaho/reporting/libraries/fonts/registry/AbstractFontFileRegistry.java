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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Creation-Date: 21.07.2007, 17:01:15
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class AbstractFontFileRegistry implements FontRegistry {
  private static final Log logger = LogFactory.getLog( AbstractFontFileRegistry.class );

  private HashMap<String, FontFileRecord> seenFiles;
  private HashMap<String, DefaultFontFamily> fontFamilies;
  private HashMap<String, DefaultFontFamily> alternateFamilyNames;
  private HashMap<String, DefaultFontFamily> fullFontNames;

  protected AbstractFontFileRegistry() {
    seenFiles = new HashMap<String, FontFileRecord>();
    this.fontFamilies = new HashMap<String, DefaultFontFamily>();
    this.alternateFamilyNames = new HashMap<String, DefaultFontFamily>();
    this.fullFontNames = new HashMap<String, DefaultFontFamily>();
  }

  protected HashMap<String, FontFileRecord> getSeenFiles() {
    return seenFiles;
  }

  protected abstract FileFilter getFileFilter();

  public void initialize() {
    registerDefaultFontPath();
    final Configuration configuration = LibFontBoot.getInstance().getGlobalConfig();
    final Iterator extraDirIt =
      configuration.findPropertyKeys( "org.pentaho.reporting.libraries.fonts.extra-font-dirs." );
    while ( extraDirIt.hasNext() ) {
      final String extraDirKey = (String) extraDirIt.next();
      final String extraDir = configuration.getConfigProperty( extraDirKey );
      final File extraDirFile = new File( extraDir );
      try {
        if ( extraDirFile.isDirectory() ) {
          registerFontPath( extraDirFile, getDefaultEncoding() );
        }
      } catch ( Exception e ) {
        logger.warn( "Extra font path " + extraDir + " could not be fully registered.", e );
      }
    }
  }

  protected String getDefaultEncoding() {
    return LibFontBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.fonts.itext.FontEncoding", EncodingRegistry.getPlatformDefaultEncoding() );
  }

  /**
   * Register os-specific font paths to the PDF-FontFactory. For unix-like operating systems, X11 is searched in
   * /usr/X11R6 and the default truetype fontpath is added. For windows the system font path is added (%windir%/fonts)
   */
  public void registerDefaultFontPath() {
    final String encoding = getDefaultEncoding();
    loadFromCache( encoding );

    final String osname = safeSystemGetProperty( "os.name", "<protected by system security>" );
    final String jrepath = safeSystemGetProperty( "java.home", "." );
    final String fs = safeSystemGetProperty( "file.separator", File.separator );

    logger.debug( "Running on operating system: " + osname );
    logger.debug( "Character encoding used as default: " + encoding );

    if ( StringUtils.startsWithIgnoreCase( osname, "mac os x" ) ) {
      final String userhome = safeSystemGetProperty( "user.home", "." );
      logger.debug( "Detected MacOS." );
      registerFontPath( new File( userhome + "/Library/Fonts" ), encoding );
      registerFontPath( new File( "/Library/Fonts" ), encoding );
      registerFontPath( new File( "/Network/Library/Fonts" ), encoding );
      registerFontPath( new File( "/System/Library/Fonts" ), encoding );
    } else if ( StringUtils.startsWithIgnoreCase( osname, "windows" ) ) {
      registerWindowsFontPath( encoding );
    } else {
      logger.debug( "Assuming unix like file structures" );
      // Assume X11 is installed in the default location.
      registerFontPath( new File( "/usr/X11R6/lib/X11/fonts" ), encoding );
      registerFontPath( new File( "/usr/share/fonts" ), encoding );
    }
    registerFontPath( new File( jrepath, "lib" + fs + "fonts" ), encoding );

    storeToCache( encoding );
    logger.info( "Completed font registration." );
  }

  protected void registerPrimaryName( final String name, final DefaultFontFamily family ) {
    this.fontFamilies.put( name, family );
  }

  protected void registerAlternativeName( final String name, final DefaultFontFamily family ) {
    this.alternateFamilyNames.put( name, family );
  }

  protected void registerFullName( final String name, final DefaultFontFamily family ) {
    this.fullFontNames.put( name, family );
  }

  protected DefaultFontFamily createFamily( final String name ) {
    final DefaultFontFamily fontFamily = this.fontFamilies.get( name );
    if ( fontFamily != null ) {
      return fontFamily;
    }

    final DefaultFontFamily createdFamily = new DefaultFontFamily( name );
    this.fontFamilies.put( name, createdFamily );
    return createdFamily;
  }

  public String[] getRegisteredFamilies() {
    return fontFamilies.keySet().toArray( new String[ fontFamilies.size() ] );
  }

  public String[] getAllRegisteredFamilies() {
    return alternateFamilyNames.keySet().toArray( new String[ alternateFamilyNames.size() ] );
  }

  public FontFamily getFontFamily( final String name ) {
    final FontFamily primary = this.fontFamilies.get( name );
    if ( primary != null ) {
      return primary;
    }
    final FontFamily secondary = this.alternateFamilyNames.get( name );
    if ( secondary != null ) {
      return secondary;
    }
    return this.fullFontNames.get( name );
  }

  protected void loadFromCache( final String encoding ) {
    final String fileName = getCacheFileName();
    if ( fileName == null ) {
      return;
    }
    loadFromCache( encoding, fileName );
  }

  protected void populateFromCache( final HashMap<String, DefaultFontFamily> cachedFontFamilies,
                                    final HashMap<String, DefaultFontFamily> cachedFullFontNames,
                                    final HashMap<String, DefaultFontFamily> cachedAlternateNames ) {
    this.fontFamilies.putAll( cachedFontFamilies );
    this.fullFontNames.putAll( cachedFullFontNames );
    this.alternateFamilyNames.putAll( cachedAlternateNames );
  }

  protected void loadFromCache( final String encoding, final String filename ) {
    final ResourceManager resourceManager = new ResourceManager();
    final File location = createStorageLocation();
    if ( location == null ) {
      return;
    }
    final File ttfCache = new File( location, filename );
    try {
      final ResourceKey resourceKey = resourceManager.createKey( ttfCache );
      final ResourceData data = resourceManager.load( resourceKey );
      final InputStream stream = data.getResourceAsStream( resourceManager );

      final HashMap<String, FontFileRecord> cachedSeenFiles;
      final HashMap<String, DefaultFontFamily> cachedFontFamilies;
      final HashMap<String, DefaultFontFamily> cachedFullFontNames;
      final HashMap<String, DefaultFontFamily> cachedAlternateNames;

      try {
        final ObjectInputStream oin = new ObjectInputStream( stream );
        final Object[] cache = (Object[]) oin.readObject();
        if ( cache.length != 5 ) {
          return;
        }
        if ( ObjectUtilities.equal( encoding, cache[ 0 ] ) == false ) {
          return;
        }
        cachedSeenFiles = (HashMap<String, FontFileRecord>) cache[ 1 ];
        cachedFontFamilies = (HashMap<String, DefaultFontFamily>) cache[ 2 ];
        cachedFullFontNames = (HashMap<String, DefaultFontFamily>) cache[ 3 ];
        cachedAlternateNames = (HashMap<String, DefaultFontFamily>) cache[ 4 ];
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
        this.getSeenFiles().putAll( cachedSeenFiles );
        populateFromCache( cachedFontFamilies, cachedFullFontNames, cachedAlternateNames );
      }
    } catch ( final ClassNotFoundException cnfe ) {
      // ignore the exception.
      logger.debug( "Failed to restore the cache: Cache was created by a different version of LibFonts" );
    } catch ( Exception e ) {
      logger.debug( "Non-Fatal: Failed to restore the cache. The cache will be rebuilt.", e );
    }
  }

  protected String getCacheFileName() {
    return null;
  }

  protected void storeToCache( final String encoding ) {
    final String cacheFileName = getCacheFileName();
    if ( cacheFileName == null ) {
      return;
    }

    final File location = createStorageLocation();
    if ( location == null ) {
      return;
    }
    location.mkdirs();
    if ( location.exists() == false || location.isDirectory() == false ) {
      return;
    }

    final File ttfCache = new File( location, cacheFileName );
    try {
      final FileOutputStream fout = new FileOutputStream( ttfCache );
      try {
        final Object[] map = new Object[ 5 ];
        map[ 0 ] = encoding;
        map[ 1 ] = getSeenFiles();
        map[ 2 ] = fontFamilies;
        map[ 3 ] = fullFontNames;
        map[ 4 ] = alternateFamilyNames;

        final ObjectOutputStream objectOut = new ObjectOutputStream( new BufferedOutputStream( fout ) );
        objectOut.writeObject( map );
        objectOut.close();
      } finally {
        try {
          fout.close();
        } catch ( IOException e ) {
          // ignore ..
          logger.debug( "Failed to store cached font data", e );
        }
      }
    } catch ( IOException e ) {
      // should not happen
      logger.debug( "Failed to store cached font data", e );
    }
  }

  /**
   * Registers the default windows font path. Once a font was found in the old seenFiles map and confirmed, that this
   * font still exists, it gets copied into the confirmedFiles map.
   *
   * @param encoding the default font encoding.
   */
  private void registerWindowsFontPath( final String encoding ) {
    logger.debug( "Found 'Windows' in the OS name, assuming DOS/Win32 structures" );
    // Assume windows
    // If you are not using windows, ignore this. This just checks if a windows system
    // directory exist and includes a font dir.

    String fontPath = null;
    final String windirs = safeSystemGetProperty( "java.library.path", null );
    final String fs = safeSystemGetProperty( "file.separator", File.separator );

    if ( windirs != null ) {
      final StringTokenizer strtok = new StringTokenizer
        ( windirs, safeSystemGetProperty( "path.separator", File.pathSeparator ) );
      while ( strtok.hasMoreTokens() ) {
        final String token = strtok.nextToken();

        if ( StringUtils.endsWithIgnoreCase( token, "System32" ) ) {
          // found windows folder ;-)
          final int lastBackslash = token.lastIndexOf( fs );
          if ( lastBackslash != -1 ) {
            fontPath = token.substring( 0, lastBackslash ) + fs + "Fonts";
            break;
          }
          // try with forward slashs. Some systems may use the unix-semantics instead.
          // (Windows accepts both characters as path-separators for historical reasons)
          final int lastSlash = token.lastIndexOf( '/' );
          if ( lastSlash != -1 ) {
            fontPath = token.substring( 0, lastSlash ) + fs + "Fonts";
            break;
          }
        }
      }
    }
    logger.debug( "Fonts located in \"" + fontPath + '\"' );
    if ( fontPath != null ) {
      final File file = new File( fontPath );
      registerFontPath( file, encoding );
    }
  }

  /**
   * Register all fonts (*.ttf files) in the given path.
   *
   * @param file     the directory that contains the font files.
   * @param encoding the encoding for the given font.
   */
  public void registerFontPath( final File file, final String encoding ) {
    if ( file.exists() && file.isDirectory() && file.canRead() ) {
      final File[] files = file.listFiles( getFileFilter() );
      final int fileCount = files.length;
      for ( int i = 0; i < fileCount; i++ ) {
        final File currentFile = files[ i ];
        if ( currentFile.isDirectory() ) {
          registerFontPath( currentFile, encoding );
        } else {
          if ( isCached( currentFile ) == false ) {
            registerFontFile( currentFile, encoding );
          }
        }
      }
    }
  }


  protected boolean isCached( final File file ) {
    try {
      final FontFileRecord stored = seenFiles.get( file.getCanonicalPath() );
      if ( stored == null ) {
        return false;
      }

      final FontFileRecord rec = new FontFileRecord( file );
      if ( stored.equals( rec ) == false ) {
        seenFiles.remove( file.getCanonicalPath() );
        return false;
      }
      return true;
    } catch ( IOException e ) {
      return false;
    }
  }

  /**
   * Register the font (must end this *.ttf) to the FontFactory.
   *
   * @param filename the filename.
   * @param encoding the encoding.
   */
  public void registerFontFile( final String filename,
                                final String encoding ) {
    final File file = new File( filename );
    registerFontFile( file, encoding );
  }

  public synchronized void registerFontFile( final File file, final String encoding ) {
    if ( getFileFilter().accept( file ) && file.exists() && file.isFile() && file.canRead() ) {
      try {
        if ( file.length() == 0 ) {
          logger.warn( "Font " + file + " is invalid [zero size]." );
          return;
        }
        if ( addFont( file, encoding ) ) {
          final FontFileRecord value = new FontFileRecord( file );
          seenFiles.put( file.getCanonicalPath(), value );
        }
      } catch ( Exception e ) {
        logger.warn( "Font " + file + " is invalid. Message:" + e.getMessage(), e );
      }
    }
  }


  /**
   * Adds the fontname by creating the basefont object. This method tries to load the fonts as embeddable fonts, if this
   * fails, it repeats the loading with the embedded-flag set to false.
   *
   * @param font     the font file name.
   * @param encoding the encoding.
   * @return true, if registration was successful, false otherwise.
   * @throws java.io.IOException if the base font file could not be read.
   */
  protected abstract boolean addFont( final File font, final String encoding )
    throws IOException;


  protected String safeSystemGetProperty( final String name,
                                          final String defaultValue ) {
    try {
      return System.getProperty( name, defaultValue );
    } catch ( SecurityException se ) {
      return defaultValue;
    }
  }


  protected boolean isCacheValid( final HashMap cachedSeenFiles ) {
    final Iterator iterator = cachedSeenFiles.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final String fullFileName = (String) entry.getKey();
      final FontFileRecord fontFileRecord = (FontFileRecord) entry.getValue();
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

  protected File createStorageLocation() {
    if ( "true".equals( LibFontBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.fonts.CacheFontRegistration" ) ) == false ) {
      return null;
    }

    final String homeDirectory = safeSystemGetProperty( "user.home", null );
    if ( homeDirectory == null ) {
      return null;
    }
    final File homeFile = new File( homeDirectory );
    if ( homeFile.isDirectory() == false ) {
      return null;
    }
    return new File( homeFile, ".pentaho/caches/libfonts2" );
  }
}

