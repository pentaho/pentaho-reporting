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

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The project information structure contains information about the current project. This is an extended version of the
 * dependency information enriched with information about the boot-process and a list of dependencies.
 * <p/>
 * This class needs to be subclassed by each project that wants to participate in the global boot process.
 *
 * @author Thomas Morgner
 */
public abstract class ProjectInformation extends DependencyInformation {
  private String copyright;
  private String bootClass;
  private ArrayList<DependencyInformation> libraries;
  private ArrayList<DependencyInformation> optionalLibraries;
  private VersionHelper versionHelper;
  private String internalName;

  /**
   * Creates a new project information object with the given name.
   *
   * @param name the name of the project, when internal and public names are equal.
   */
  protected ProjectInformation( final String name ) {
    this( name, name );
  }

  /**
   * Creates a new project information object with the given name. The internal name is used to lookup the version
   * information in the manifest file, while the public name is presented to the humans.
   *
   * @param internalName the internal name of the project.
   * @param publicName   the public name of the project.
   */
  protected ProjectInformation( final String internalName, final String publicName ) {
    super( publicName );
    if ( internalName == null ) {
      this.internalName = publicName;
    } else {
      this.internalName = internalName;
    }
    this.libraries = new ArrayList<DependencyInformation>();
    optionalLibraries = new ArrayList<DependencyInformation>();
  }

  /**
   * Returs a version helper for this project. The version helper is used to extract the versioning information from the
   * manifest file of the project's JAR.
   *
   * @return the version helper, never null.
   */
  private synchronized VersionHelper getVersionHelper() {
    if ( versionHelper == null ) {
      versionHelper = new VersionHelper( this );
    }
    return versionHelper;
  }

  /**
   * Returns the copyright string for thie project.
   *
   * @return the copyright string (might be null).
   */
  public String getCopyright() {
    return copyright;
  }

  /**
   * Updates the copyright string for thie project.
   *
   * @param copyright the copyright string.
   */
  protected void setCopyright( final String copyright ) {
    this.copyright = copyright;
  }

  /**
   * Returns the internal name of the project.
   *
   * @return the internal name, never null.
   */
  public String getInternalName() {
    return internalName;
  }

  /**
   * Returns the boot class.
   *
   * @return the bootclass (might be null).
   */
  public String getBootClass() {
    return bootClass;
  }

  /**
   * Redefines the boot class.
   *
   * @param bootClass the bootclass (might be null).
   */
  protected void setBootClass( final String bootClass ) {
    this.bootClass = bootClass;
  }

  /**
   * Returns a list of libraries used by the project.
   *
   * @return the list of libraries.
   */
  public DependencyInformation[] getLibraries() {
    return this.libraries.toArray
      ( new DependencyInformation[ this.libraries.size() ] );
  }

  /**
   * Adds a library.
   *
   * @param library the library.
   */
  public void addLibrary( final DependencyInformation library ) {
    if ( library == null ) {
      throw new NullPointerException();
    }
    if ( this.libraries.contains( library ) ) {
      throw new NullPointerException();
    }

    this.libraries.add( library );
  }

  /**
   * Returns a list of optional libraries used by the project.
   *
   * @return the list of libraries.
   */
  public DependencyInformation[] getOptionalLibraries() {
    return optionalLibraries.toArray( new DependencyInformation[ optionalLibraries.size() ] );
  }

  /**
   * Adds an optional library. These libraries will be booted, if they define a boot class. A missing class is
   * considered non-fatal and it is assumed that the programm knows how to handle that.
   *
   * @param libraryClass the library.
   */
  protected void addOptionalLibrary( final String libraryClass ) {
    if ( libraryClass == null ) {
      throw new NullPointerException( "Library classname must be given." );
    }
    final DependencyInformation depInfo = loadLibrary( libraryClass );
    if ( depInfo != null ) {
      this.optionalLibraries.add( depInfo );
    }
  }

  /**
   * Tries to load the dependency information for the given class.
   *
   * @param classname the classname of the class that contains the <code>public static DependencyInformation
   *                  getInstance()</code> method.
   * @return the dependency information for the library or null, if the library's project-info class could not be
   * loaded.
   */
  private DependencyInformation loadLibrary( final String classname ) {
    if ( classname == null ) {
      return null;
    }
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( getClass() );
      final Class c = Class.forName( classname, false, loader );
      try {
        // This cast is necessary for JDK 1.5 or later
        final Method m = c.getMethod( "getInstance", (Class[]) null );
        return (DependencyInformation) m.invoke( null, (Object[]) null );
      } catch ( Exception e ) {
        // ok, fall back ...
      }
      return (DependencyInformation) c.newInstance();
    } catch ( Exception e ) {
      // ok, this library has no 'getInstance()' method. Check the
      // default constructor ...
      return null;
    }
  }

  /**
   * Adds an optional library. These libraries will be booted, if they define a boot class. A missing class is
   * considered non-fatal and it is assumed that the programm knows how to handle that.
   *
   * @param library the library.
   */
  protected void addOptionalLibrary( final DependencyInformation library ) {
    if ( library == null ) {
      throw new NullPointerException( "Library must be given." );
    }
    this.optionalLibraries.add( library );
  }

  /**
   * Returns the version number from the Manifest.
   *
   * @return the version, or null if no version information is known.
   */
  public String getVersion() {
    return getVersionHelper().getVersion();
  }

  /**
   * Returns the product ID from the Manifest.
   *
   * @return the product ID, or null if none is specified in the manifest.
   */
  public String getProductId() {
    return getVersionHelper().getProductId();
  }

  /**
   * Returns the release milestone number from the Manifest.
   *
   * @return the release milestone number, or null if none is specified in the manifest.
   */
  public String getReleaseMilestone() {
    return getVersionHelper().getReleaseMilestone();
  }

  /**
   * Returns the release minor version number from the Manifest.
   *
   * @return the release minor version number, or null if none is specified in the manifest.
   */
  public String getReleaseMinor() {
    return getVersionHelper().getReleaseMinor();
  }

  /**
   * Returns the release major version number from the Manifest.
   *
   * @return the release major version number, or null if none is specified in the manifest.
   */
  public String getReleaseMajor() {
    return getVersionHelper().getReleaseMajor();
  }

  /**
   * Returns the release candidate token from the Manifest.
   *
   * @return the release candidate token, or null if none is specified in the manifest.
   */
  public String getReleaseCandidateToken() {
    return getVersionHelper().getReleaseCandidateToken();
  }

  /**
   * Returns the release number from the Manifest.
   *
   * @return the release number, or null if none is specified in the manifest.
   */
  public String getReleaseNumber() {
    return getVersionHelper().getReleaseNumber();
  }

  /**
   * Returns the release build number from the Manifest.
   *
   * @return the release build number, or null if none is specified in the manifest.
   */
  public String getReleaseBuildNumber() {
    return versionHelper.getReleaseBuildNumber();
  }
}
