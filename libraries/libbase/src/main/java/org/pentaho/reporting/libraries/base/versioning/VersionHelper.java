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

package org.pentaho.reporting.libraries.base.versioning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * A utility class for reading versioning information from a Manifest file.
 *
 * @author Thomas Morgner
 */
public class VersionHelper {
  private static class ManifestCache {
    private Map<String, Manifest> manifests;
    private Map<String, Manifest> manifestsByURL;

    public ManifestCache() {
      manifests = new HashMap<String, Manifest>();
      manifestsByURL = new HashMap<String, Manifest>();
    }

    public synchronized Manifest get( final String title ) {
      return manifests.get( title );
    }

    public synchronized void set( final String title, final String url, final Manifest manifest ) {
      if ( title != null ) {
        manifests.put( title, manifest );
      }
      manifestsByURL.put( url, manifest );
    }

    public synchronized Manifest getByURL( final String url ) {
      return manifestsByURL.get( url );
    }
  }

  private static final Log logger = LogFactory.getLog( VersionHelper.class );
  private static final ManifestCache manifestCache = new ManifestCache();
  public static final String SNAPSHOT_TOKEN = "SNAPSHOT";
  private String version;
  private String title;
  private String productId;
  private String releaseMilestone;
  private String releaseMinor;
  private String releaseMajor;
  private String releaseNumber;
  private String releasePatch;
  private String releaseBuildNumber;
  private ProjectInformation projectInformation;


  /**
   * Loads the versioning information for the given project-information structure using the project information's
   * internal name as lookup key.
   *
   * @param projectInformation the project we load information for.
   */
  public VersionHelper( final ProjectInformation projectInformation ) {
    if ( projectInformation == null ) {
      throw new NullPointerException();
    }

    this.projectInformation = projectInformation;

    Manifest manifest = manifestCache.get( projectInformation.getInternalName() );
    if ( manifest == null ) {
      final ClassLoader loader = projectInformation.getClass().getClassLoader();
      try {
        final Enumeration resources = loader.getResources( "META-INF/MANIFEST.MF" );
        while ( resources.hasMoreElements() ) {
          final URL url = (URL) resources.nextElement();
          final String urlAsText = url.toURI().toString();
          Manifest maybeManifest = manifestCache.getByURL( urlAsText );
          if ( maybeManifest == null ) {
            final InputStream inputStream = url.openStream();
            try {
              maybeManifest = new Manifest( new BufferedInputStream( inputStream ) );
            } finally {
              inputStream.close();
            }
          }

          final Attributes attr = getAttributes( maybeManifest, projectInformation.getInternalName() );
          final String maybeTitle = getValue( attr, "Implementation-ProductID", null );
          if ( maybeTitle != null ) {
            manifestCache.set( maybeTitle, urlAsText, maybeManifest );
            if ( maybeTitle.equals( projectInformation.getInternalName() ) ) {
              manifest = maybeManifest;
              break;
            }
          } else {
            manifestCache.set( null, urlAsText, maybeManifest );
          }
        }

      } catch ( Exception e ) {
        // Ignore; Maybe log.
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Failed to read manifest for retrieving library version information for " +
            projectInformation.getProductId(), e );
        }
      }
    }
    if ( manifest != null ) {
      init( manifest );
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to create version information for " + projectInformation.getInternalName() );
      }
      version = "TRUNK.development";
      title = projectInformation.getInternalName();
      productId = projectInformation.getInternalName();
      releaseMajor = "999";
      releaseMinor = "999";
      releaseMilestone = "999";
      releasePatch = "0";
      releaseBuildNumber = SNAPSHOT_TOKEN;
      releaseNumber = createReleaseVersion();
    }
  }

  /**
   * Initializes the instance, reaading the properties from the input stream.
   *
   * @param props the manifest.
   * @return true, if the manifest contains version information about this library, false otherwise.
   */
  private boolean init( final Manifest props ) {
    try {
      final Attributes attr = getAttributes( props, projectInformation.getInternalName() );
      final String maybeTitle = getValue( attr, "Implementation-ProductID", null );
      if ( ObjectUtilities.equal( projectInformation.getInternalName(), maybeTitle ) == false ) {
        return false;
      }

      title = getValue( attr, "Implementation-Title", maybeTitle );
      version = getValue( attr, "Implementation-Version", "" );

      parseVersion( version );

      productId = maybeTitle;
      if ( productId.length() == 0 ) {
        productId = createProductId();
      }

      return true;
    } catch ( final Exception e ) {
      return false;
    }
  }

  protected void parseVersion( String version ) {
    if ( version == null || version.length() == 0 ) {
      version = "TRUNK.development";
    }

    releaseMajor = "999";
    releaseMinor = "999";
    releaseMilestone = "999";
    releasePatch = "0";
    releaseBuildNumber = SNAPSHOT_TOKEN;
    if ( version.startsWith( "TRUNK" ) == false ) {
      // format is something like 3.8.0[.x]-GA.12345
      final int dashPos = version.indexOf( '-' );
      final String versionNumber;
      final String implIndicator;
      if ( dashPos != -1 ) {
        versionNumber = version.substring( 0, dashPos );
        implIndicator = version.substring( dashPos + 1 );
      } else {
        versionNumber = version;
        implIndicator = "";
      }
      if ( StringUtils.isEmpty( versionNumber ) == false ) {
        final StringTokenizer tokNum = new StringTokenizer( versionNumber, "." );
        if ( tokNum.hasMoreTokens() ) {
          releaseMajor = tokNum.nextToken();
        }
        if ( tokNum.hasMoreTokens() ) {
          releaseMinor = tokNum.nextToken();
        }
        if ( tokNum.hasMoreTokens() ) {
          releaseMilestone = tokNum.nextToken();
        }
        if ( tokNum.hasMoreTokens() ) {
          releasePatch = tokNum.nextToken();
        }
        final StringTokenizer tokImpl = new StringTokenizer( implIndicator, "." );
        if ( tokImpl.hasMoreTokens() ) {
          releaseBuildNumber = tokImpl.nextToken();
        }
      }
    }
    releaseNumber = createReleaseVersion();
  }

  /**
   * Looks up the attributes for the given module specified by <code>name</code> in the given Manifest.
   *
   * @param props the manifest where to search for the attributes.
   * @param name  the name of the module.
   * @return the attributes for the module or the main attributes if the jar contains no such module.
   */
  private Attributes getAttributes( final Manifest props, final String name ) {
    final Attributes attributes = props.getAttributes( name );
    if ( attributes == null ) {
      return props.getMainAttributes();
    }
    return attributes;
  }

  /**
   * Looks up a single value in the given attribute collection using the given key. If the key is not contained in the
   * attributes, this method returns the default value specified as parameter.
   *
   * @param attrs        the attributes where to lookup the key.
   * @param name         the name of the key to use for the lookup.
   * @param defaultValue the default value to return in case the attributes contain no such key.
   * @return the value from the attributes or the default values.
   */
  private String getValue( final Attributes attrs, final String name, final String defaultValue ) {
    final String value = attrs.getValue( name );
    if ( value == null ) {
      return defaultValue;
    }
    return value.trim();
  }

  /**
   * Creates a product-id string, which is the implementation title plus the optional version information.
   *
   * @return the product id string.
   */
  private String createProductId() {
    if ( version.trim().length() == 0 ) {
      return title;
    }
    return title + '-' + version;
  }

  /**
   * Creates a version string using the major, minor and milestone version information and the build number.
   *
   * @return the release version.
   */
  private String createReleaseVersion() {
    final StringBuilder buffer = new StringBuilder( 50 );
    buffer.append( releaseMajor );
    buffer.append( '.' );
    buffer.append( releaseMinor );
    buffer.append( '.' );
    buffer.append( releaseMilestone );
    if ( releasePatch.length() > 0 ) {
      buffer.append( '-' );
      buffer.append( releasePatch );
    }
    if ( releaseBuildNumber.length() > 0 ) {
      buffer.append( " (Build " );
      buffer.append( releaseBuildNumber );
      buffer.append( ')' );
    }
    return buffer.toString();
  }

  /**
   * Returns the full version string as computed by createVersion().
   *
   * @return the version string.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns the implementation title as specified in the manifest.
   *
   * @return the implementation title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the product id as computed by createProductId().
   *
   * @return the product id.
   * @see #createProductId()
   */
  public String getProductId() {
    return productId;
  }

  /**
   * Returns the release milestone number. Defaults to 999 if not given in the manifest.
   *
   * @return the milestone number.
   */
  public String getReleaseMilestone() {
    return releaseMilestone;
  }

  /**
   * Returns the release minor number. Defaults to 999 if not given in the manifest.
   *
   * @return the minor version number.
   */
  public String getReleaseMinor() {
    return releaseMinor;
  }

  /**
   * Returns the release major number. Defaults to 999 if not given in the manifest.
   *
   * @return the major version number.
   */
  public String getReleaseMajor() {
    return releaseMajor;
  }

  /**
   * Returns the release candidate token. Defaults to 999 if not given in the manifest.
   *
   * @return the candidate token.
   * @deprecated No longer used.
   */
  @Deprecated
  public String getReleaseCandidateToken() {
    return "";
  }

  /**
   * Returns the release patch number. Defaults to zero if not given in the manifest.
   *
   * @return the patch version number.
   */
  public String getReleasePatch() {
    return releasePatch;
  }

  /**
   * Returns the release number.
   *
   * @return the release number.
   */
  public String getReleaseNumber() {
    return releaseNumber;
  }

  /**
   * Returns the release build number.
   *
   * @return the build-number).
   */
  public String getReleaseBuildNumber() {
    return releaseBuildNumber;
  }
}
