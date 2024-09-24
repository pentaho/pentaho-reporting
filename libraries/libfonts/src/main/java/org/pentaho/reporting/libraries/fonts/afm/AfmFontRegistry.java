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

package org.pentaho.reporting.libraries.fonts.afm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.AbstractFontFileRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Creation-Date: 21.07.2007, 20:14:05
 *
 * @author Thomas Morgner
 */
public class AfmFontRegistry extends AbstractFontFileRegistry {
  private static final Log logger = LogFactory.getLog( AfmFontRegistry.class );

  /**
   * The font path filter is used to collect font files and directories during the font path registration.
   */
  private static class FontPathFilter implements FileFilter {
    /**
     * Default Constructor.
     */
    protected FontPathFilter() {
    }

    /**
     * Tests whether or not the specified abstract pathname should be included in a pathname list.
     *
     * @param pathname The abstract pathname to be tested
     * @return <code>true</code> if and only if <code>pathname</code> should be included
     */
    public boolean accept( final File pathname ) {
      if ( pathname.canRead() == false ) {
        return false;
      }
      if ( pathname.isDirectory() ) {
        return true;
      }
      final String name = pathname.getName();
      return StringUtils.endsWithIgnoreCase( name, ".afm" );
    }

  }

  /**
   * The singleton instance of the font path filter.
   */
  private static final FontPathFilter FONTPATHFILTER = new FontPathFilter();

  /**
   * Fonts stored by name.
   */

  public AfmFontRegistry() {
  }

  protected FileFilter getFileFilter() {
    return FONTPATHFILTER;
  }

  public FontMetricsFactory createMetricsFactory() {
    // this is a todo - for now we rely on itext
    throw new UnsupportedOperationException();
  }

  public FontCache getSecondLevelCache() {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds the fontname by creating the basefont object. This method tries to load the fonts as embeddable fonts, if this
   * fails, it repeats the loading with the embedded-flag set to false.
   *
   * @param font     the font file name.
   * @param encoding the encoding.
   * @throws java.io.IOException if the base font file could not be read.
   */
  public boolean addFont( final File font, final String encoding ) throws IOException {
    final String fileName = font.getCanonicalPath();
    final String filePfbName = fileName.substring( 0, fileName.length() - 3 ) + "pfb";
    final File filePfb = new File( filePfbName );
    boolean embedded = true;
    if ( filePfb.exists() == false ||
      filePfb.isFile() == false ||
      filePfb.canRead() == false ) {
      logger.warn( "Cannot embedd font: " + filePfb + " is missing for " + font );
      embedded = false;
    }

    final AfmFont pfmFont = new AfmFont( font, embedded );
    registerFont( pfmFont );
    pfmFont.dispose();
    return true;
  }

  private void registerFont( final AfmFont font ) throws IOException {
    final String windowsName = font.getFamilyName();
    final String postscriptName = font.getFontName();

    final DefaultFontFamily fontFamily = createFamily( windowsName );
    fontFamily.addFontRecord( new AfmFontRecord( font, fontFamily ) );

    registerPrimaryName( windowsName, fontFamily );
    registerAlternativeName( windowsName, fontFamily );
    registerAlternativeName( postscriptName, fontFamily );

    registerFullName( windowsName, fontFamily );
    registerFullName( postscriptName, fontFamily );
  }

  protected String getCacheFileName() {
    return "afm-fontcache.ser";
  }
}
