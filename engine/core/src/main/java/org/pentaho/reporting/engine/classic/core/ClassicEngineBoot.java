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

package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.DefaultModuleInfo;
import org.pentaho.reporting.libraries.base.boot.PackageManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.config.SystemPropertyConfiguration;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * An utility class to safely boot and initialize the Pentaho-Reporting library. This class should be called before
 * using the Pentaho-Reporting classes, to make sure that all subsystems are initialized correctly and in the correct
 * order.
 * <p/>
 * Application developers should make sure, that the booting is done before any Pentaho-Reporting functions are used. If
 * the system has not be initialized by booting this class, anything can happen and the results of all functionality of
 * this reporting engine will be undefined.
 * <p/>
 * Additional modules can be specified by defining the system property <code>"org.pentaho.reporting.engine.classic
 * .core.boot.Modules"</code>. The property expects a comma-separated list of
 * {@link org.pentaho.reporting.libraries.base.boot.Module} implementations.
 * <p/>
 * Booting should be done by aquirering a new boot instance using {@link ClassicEngineBoot#getInstance()} and then
 * starting the boot process with {@link ClassicEngineBoot#start()}.
 *
 * @author Thomas Morgner
 */
public class ClassicEngineBoot extends AbstractBoot {
  public static final int VERSION_TRUNK = ClassicEngineBoot.computeVersionId( 999, 999, 999 );
  public static final int VERSION_3_8 = ClassicEngineBoot.computeVersionId( 3, 8, 0 );
  public static final int VERSION_3_9 = ClassicEngineBoot.computeVersionId( 3, 9, 0 );
  public static final int VERSION_4_0 = ClassicEngineBoot.computeVersionId( 4, 0, 0 );

  public static final String INDEX_COLUMN_PREFIX = "::column::";
  public static final String METADATA_NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/classic/metadata/1.0";
  public static final String DATASCHEMA_NAMESPACE =
      "http://reporting.pentaho.org/namespaces/engine/classic/dataschema/1.0";
  public static final String BUNDLE_TYPE = "application/vnd.pentaho.reporting.classic";

  private static final Log logger = LogFactory.getLog( ClassicEngineBoot.class );

  /**
   * A wrappper around the user supplied global configuration.
   */
  private static class UserConfigWrapper extends HierarchicalConfiguration {
    /**
     * The wrapped configuration.
     */
    private Configuration wrappedConfiguration;

    /**
     * Default constructor.
     */
    protected UserConfigWrapper() {
      this( null );
    }

    /**
     * Creates a new user-configuration wrapper for the given configuration.
     *
     * @param config
     *          the user-provided configuration that should be wrapped.
     */
    protected UserConfigWrapper( final Configuration config ) {
      this.wrappedConfiguration = config;
    }

    /**
     * Sets a new configuration. This configuration will be inserted into the report configuration hierarchy. Set this
     * property to null to disable the user defined configuration.
     *
     * @param wrappedConfiguration
     *          the wrapped configuration.
     */
    public void setWrappedConfiguration( final Configuration wrappedConfiguration ) {
      this.wrappedConfiguration = wrappedConfiguration;
    }

    /**
     * Returns the user supplied global configuration, if exists.
     *
     * @return the user configuration.
     */
    public Configuration getWrappedConfiguration() {
      return wrappedConfiguration;
    }

    /**
     * Returns the configuration property with the specified key.
     *
     * @param key
     *          the property key.
     * @return the property value.
     */
    public String getConfigProperty( final String key ) {
      if ( wrappedConfiguration == null ) {
        return getParentConfig().getConfigProperty( key );
      }

      final String retval = wrappedConfiguration.getConfigProperty( key );
      if ( retval != null ) {
        return retval;
      }
      return getParentConfig().getConfigProperty( key );
    }

    /**
     * Returns the configuration property with the specified key (or the specified default value if there is no such
     * property).
     * <p/>
     * If the property is not defined in this configuration, the code will lookup the property in the parent
     * configuration.
     *
     * @param key
     *          the property key.
     * @param defaultValue
     *          the default value.
     * @return the property value.
     */
    public String getConfigProperty( final String key, final String defaultValue ) {
      if ( wrappedConfiguration == null ) {
        return getParentConfig().getConfigProperty( key, defaultValue );
      }

      final String retval = wrappedConfiguration.getConfigProperty( key, null );
      if ( retval != null ) {
        return retval;
      }
      return getParentConfig().getConfigProperty( key, defaultValue );
    }

    /**
     * Sets a configuration property.
     *
     * @param key
     *          the property key.
     * @param value
     *          the property value.
     */
    public void setConfigProperty( final String key, final String value ) {
      if ( wrappedConfiguration instanceof ModifiableConfiguration ) {
        final ModifiableConfiguration modConfiguration = (ModifiableConfiguration) wrappedConfiguration;
        modConfiguration.setConfigProperty( key, value );
      }
    }

    /**
     * Returns all defined configuration properties for the report. The enumeration contains all keys of the changed
     * properties, properties set from files or the system properties are not included.
     *
     * @return all defined configuration properties for the report.
     */
    public Enumeration<String> getConfigProperties() {
      if ( wrappedConfiguration instanceof ModifiableConfiguration ) {
        final ModifiableConfiguration modConfiguration = (ModifiableConfiguration) wrappedConfiguration;
        return modConfiguration.getConfigProperties();
      }
      return super.getConfigProperties();
    }
  }

  /**
   * The singleton instance of the Boot class.
   */
  private static ClassicEngineBoot instance;
  /**
   * The project info contains all meta data about the project.
   */
  private ProjectInformation projectInfo;

  /**
   * Holds a possibly empty reference to a user-supplied Configuration implementation.
   */
  private static final UserConfigWrapper configWrapper = new UserConfigWrapper();

  /**
   * Creates a new instance.
   */
  private ClassicEngineBoot() {
    projectInfo = ClassicEngineInfo.getInstance();
  }

  /**
   * Returns the singleton instance of the boot utility class.
   *
   * @return the boot instance.
   */
  public static synchronized ClassicEngineBoot getInstance() {
    if ( instance == null ) {
      instance = new ClassicEngineBoot();
    }
    return instance;
  }

  /**
   * Returns the current global configuration as modifiable instance. This is exactly the same as casting the global
   * configuration into a ModifableConfiguration instance.
   * <p/>
   * This is a convinience function, as all programmers are lazy.
   *
   * @return the global config as modifiable configuration.
   */
  public ModifiableConfiguration getEditableConfig() {
    return (ModifiableConfiguration) getGlobalConfig();
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return projectInfo;
  }

  /**
   * Loads the configuration. This will be called exactly once.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    final HierarchicalConfiguration globalConfig =
        createDefaultHierarchicalConfiguration( "/org/pentaho/reporting/engine/classic/core/classic-engine.properties",
            "/classic-engine.properties", false, ClassicEngineBoot.class );

    globalConfig.insertConfiguration( ClassicEngineBoot.configWrapper );

    final SystemPropertyConfiguration systemConfig = new SystemPropertyConfiguration();
    globalConfig.insertConfiguration( systemConfig );
    return globalConfig;
  }

  /**
   * Performs the actual boot process.
   */
  protected void performBoot() {
    if ( ClassicEngineBoot.isStrictFP() == false ) {
      ClassicEngineBoot.logger.warn( "The used VM seems to use a non-strict floating point arithmetics" ); // NON-NLS
      ClassicEngineBoot.logger.warn( "Layouts computed with this Java Virtual Maschine may be invalid." ); // NON-NLS
      ClassicEngineBoot.logger.warn( "JFreeReport and the library 'iText' depend on the strict floating point rules" ); // NON-NLS
      ClassicEngineBoot.logger.warn( "of Java1.1 as implemented by the Sun Virtual Maschines." ); // NON-NLS
      ClassicEngineBoot.logger.warn( "If you are using the BEA JRockit VM, start the Java VM with the option" ); // NON-NLS
      ClassicEngineBoot.logger.warn( "'-Xstrictfp' to restore the default behaviour." ); // NON-NLS
    }

    final PackageManager mgr = getPackageManager();

    mgr.addModule( ClassicEngineCoreModule.class.getName() );
    mgr.load( "org.pentaho.reporting.engine.classic.core.modules." ); // NON-NLS
    mgr.load( "org.pentaho.reporting.engine.classic.extensions.modules." ); // NON-NLS
    mgr.load( "org.pentaho.reporting.engine.classic.extensions.datasources." ); // NON-NLS
    mgr.load( "org.pentaho.reporting.engine.classic.core.userdefined.modules." ); // NON-NLS

    bootAdditionalModules();
    mgr.initializeModules();

    if ( mgr.isModuleAvailable( ClassicEngineCoreModule.class.getName() ) == false ) {
      throw new IllegalStateException( "Booting the report-engine failed." );
    }

    StyleKey.lock();
  }

  /**
   * Boots modules, which have been spcified in the "org.pentaho.reporting.engine.classic.core.boot.Modules"
   * configuration parameter.
   */
  private void bootAdditionalModules() {
    try {
      final String bootModules =
          getGlobalConfig().getConfigProperty( "org.pentaho.reporting.engine.classic.core.boot.Modules" ); // NON-NLS
      if ( bootModules != null ) {
        final CSVTokenizer csvToken = new CSVTokenizer( bootModules, "," );
        while ( csvToken.hasMoreTokens() ) {
          final String token = csvToken.nextToken();
          getPackageManager().load( token );
        }
      }
    } catch ( SecurityException se ) {
      // we'll ignore any Security exception ..
      ClassicEngineBoot.logger.info( "Security settings forbid to check the system properties for extension modules." ); // NON-NLS
    } catch ( Exception se ) {
      ClassicEngineBoot.logger.error( "An error occured while checking the system properties for extension modules.", // NON-NLS
          se );
    }
  }

  /**
   * This method returns true on non-strict floating point systems.
   * <p/>
   * Since Java 1.2 Virtual Maschines may implement the floating point arithmetics in a more performant way, which does
   * not put the old strict constraints on the floating point types <code>float</code> and <code>double</code>.
   * <p/>
   * As iText and this library requires strict (in the sense of Java1.1) floating point operations, we have to test for
   * that feature here.
   * <p/>
   * The only known VM that seems to implement that feature is the JRockit VM. The strict mode can be restored on that
   * VM by adding the "-Xstrictfp" VM parameter.
   *
   * @return true, if the VM uses strict floating points by default, false otherwise.
   */
  private static boolean isStrictFP() {
    final double d = 8.0e+307;
    final double result1 = 4.0 * d * 0.5;
    final double result2 = 2.0 * d;
    return ( result1 != result2 && ( result1 == Double.POSITIVE_INFINITY ) );
  }

  /**
   * Returns the user supplied global configuration.
   *
   * @return the user configuration, if any.
   */
  public static Configuration getUserConfig() {
    return configWrapper.getWrappedConfiguration();
  }

  /**
   * Defines the global user configuration.
   *
   * @param config
   *          the user configuration.
   */
  public static void setUserConfig( final Configuration config ) {
    configWrapper.setWrappedConfiguration( config );
  }

  /**
   * A helper method that checks, whether a given module is available. The result of this method is undefined if the
   * system has no been booted yet.
   *
   * @param moduleClass
   *          the class-name of the module that should be tested.
   * @return true, if the module is available and has been initialized correctly, false otherwise.
   */
  public boolean isModuleAvailable( final String moduleClass ) {
    return getPackageManager().isModuleAvailable( new DefaultModuleInfo( moduleClass, null, null, null ) );
  }

  public enum VersionValidity {
    VALID, INVALID_RELEASE, INVALID_PATCH
  }

  public static int parseVersionId( final String text ) {
    final StringTokenizer strtok = new StringTokenizer( text, "." );
    if ( strtok.countTokens() == 3 ) {
      final int major = ParserUtil.parseInt( strtok.nextToken(), -1 );
      final int minor = ParserUtil.parseInt( strtok.nextToken(), -1 );
      final int patch = ParserUtil.parseInt( strtok.nextToken(), -1 );
      if ( major == -1 || minor == -1 || patch == -1 ) {
        return -1;
      } else {
        return ( ClassicEngineBoot.computeVersionId( major, minor, patch ) );
      }
    } else {
      return -1;
    }
  }

  public static String printVersion( final int versionId ) {
    if ( versionId <= 0 || versionId > 999000000 ) {
      return "TRUNK";
    }

    final int patch = versionId % 1000;
    final int minor = ( versionId / 1000 ) % 1000;
    final int major = ( versionId / 1000000 );
    return String.format( "%d.%d.%d", major, minor, patch );
  }

  public static int computeCurrentVersionId() {
    final int releaseMajor = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMajor(), 999 );
    final int releaseMinor = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMinor(), 999 );
    final int releasePatch = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMilestone(), 999 );
    final int version = computeVersionId( releaseMajor, releaseMinor, releasePatch );
    if ( version == 0 ) {
      return VERSION_TRUNK;
    }
    return version;
  }

  public static int computeVersionId( final int prptVersionMajorRaw, final int prptVersionMinorRaw,
      final int prptVersionPatchRaw ) {
    return prptVersionMajorRaw * 1000000 + prptVersionMinorRaw * 1000 + prptVersionPatchRaw;
  }

  public static VersionValidity isValidVersion( final int prptVersionMajorRaw, final int prptVersionMinorRaw,
      final int prptVersionPatchRaw ) {
    return getInstance().isValidVersion( prptVersionMajorRaw, prptVersionMinorRaw, prptVersionPatchRaw,
        ClassicEngineInfo.getInstance() );
  }

  protected VersionValidity isValidVersion( final int prptVersionMajorRaw, final int prptVersionMinorRaw,
      final int prptVersionPatchRaw, final ProjectInformation info ) {
    final int releaseMajor = ParserUtil.parseInt( info.getReleaseMajor(), 999 );
    final int releaseMinor = ParserUtil.parseInt( info.getReleaseMinor(), 999 );
    final int releasePatch = ParserUtil.parseInt( info.getReleaseMilestone(), 999 );
    if ( computeVersionId( prptVersionMajorRaw, prptVersionMinorRaw, prptVersionPatchRaw ) == VERSION_TRUNK ) {
      return VersionValidity.VALID;
    }

    if ( ( prptVersionMajorRaw * 1000 + prptVersionMinorRaw ) > ( releaseMajor * 1000 + releaseMinor ) ) {
      return VersionValidity.INVALID_RELEASE;
    }

    if ( ( prptVersionMajorRaw * 1000 + prptVersionMinorRaw ) == ( releaseMajor * 1000 + releaseMinor ) ) {
      if ( prptVersionPatchRaw > releasePatch ) {
        return VersionValidity.INVALID_PATCH;
      }
    }
    return VersionValidity.VALID;
  }

  public static boolean isEnforceCompatibilityFor( final int level, final int prptVersionMajorRaw,
      final int prptVersionMinorRaw ) {
    return isEnforceCompatibilityFor( level, prptVersionMajorRaw, prptVersionMinorRaw, 999 );
  }

  public static boolean isEnforceCompatibilityFor( final int level, final int prptVersionMajorRaw,
      final int prptVersionMinorRaw, final int prptVersionPatchRaw ) {
    if ( level == -1 ) {
      return false;
    }
    return level <= computeVersionId( prptVersionMajorRaw, prptVersionMinorRaw, prptVersionPatchRaw );
  }
}
