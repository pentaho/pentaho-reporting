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

package org.pentaho.reporting.libraries.base.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.PropertyFileConfiguration;
import org.pentaho.reporting.libraries.base.config.SystemPropertyConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * The common base for all Boot classes.
 * <p/>
 * This initializes the subsystem and all dependent subsystems. Implementors of this class have to provide a public
 * static getInstance() method which returns a singleton instance of the booter implementation.
 * <p/>
 * Further creation of Boot object should be prevented using protected or private constructors in that class, or proper
 * singleton behaviour cannot be guaranteed.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractBoot implements SubSystem {
  /**
   * The logger for this class.
   */
  private static final Log LOGGER = LogFactory.getLog( AbstractBoot.class );

  /**
   * The configuration wrapper around the plain configuration.
   */
  private ExtendedConfigurationWrapper extWrapper;

  /**
   * A packageManager instance of the package manager.
   */
  private PackageManager packageManager;

  /**
   * Global configuration.
   */
  private Configuration globalConfig;

  /**
   * A flag indicating whether the booting is currenly in progress.
   */
  private boolean bootInProgress;

  /**
   * A flag indicating whether the booting is complete.
   */
  private boolean bootDone;

  /**
   * The reason why booting failed for easier debugging or logging.
   */
  private Exception bootFailed;
  private ObjectFactory objectFactory;

  /**
   * Default constructor.
   */
  protected AbstractBoot() {
  }

  /**
   * Returns the packageManager instance of the package manager.
   *
   * @return The package manager.
   */
  public synchronized PackageManager getPackageManager() {
    if ( this.packageManager == null ) {
      this.packageManager = new PackageManager( this );
    }
    return this.packageManager;
  }

  /**
   * Returns the global configuration.
   *
   * @return The global configuration.
   */
  public synchronized Configuration getGlobalConfig() {
    if ( this.globalConfig == null ) {
      this.globalConfig = loadConfiguration();
    }
    return this.globalConfig;
  }

  /**
   * Checks, whether the booting is in progress.
   *
   * @return true, if the booting is in progress, false otherwise.
   */
  public final synchronized boolean isBootInProgress() {
    return this.bootInProgress;
  }

  /**
   * Checks, whether the booting is complete.
   *
   * @return true, if the booting is complete, false otherwise.
   */
  public synchronized boolean isBootDone() {
    return this.bootDone;
  }

  public String getConfigurationDomain() {
    return getProjectInfo().getProductId();
  }

  /**
   * Loads the configuration. This will be called exactly once.
   *
   * @return The configuration.
   */
  protected abstract Configuration loadConfiguration();

  /**
   * Starts the boot process. The boot process is synchronized and will block if parallel booting is not finished yet.
   * Any failure in booting will set the <code>bootFailed</code> property to true. If booting is finished, the
   * <code>bootDone</code> property is set to true.
   */
  public final void start() {

    synchronized( this ) {
      if ( isBootDone() ) {
        return;
      }
      if ( isBootFailed() ) {
        LOGGER.error( getClass() + " failed to boot: " + bootFailed.getMessage() );
      }
      while ( isBootInProgress() ) {
        try {
          wait();
        } catch ( InterruptedException e ) {
          // ignore ..
        }
      }
      if ( isBootDone() ) {
        notifyAll();
        return;
      }
      this.bootInProgress = true;
    }

    try {
      // boot dependent libraries ...
      final ProjectInformation info = getProjectInfo();
      if ( info != null ) {
        performBootDependencies( info.getLibraries() );
        performBootDependencies( info.getOptionalLibraries() );
      }

      performBoot();
      if ( LOGGER.isInfoEnabled() ) {
        if ( info != null ) {
          LOGGER.info( info.getName() + ' ' + info.getVersion() + " started." );
        } else {
          LOGGER.info( getClass() + " started." );
        }
      }
    } catch ( Exception e ) {
      LOGGER.error( getClass() + " failed to boot: ", e );
      this.bootFailed = e;
    } finally {
      synchronized( this ) {
        this.bootInProgress = false;
        this.bootDone = true;
        notifyAll();
      }
    }
  }

  /**
   * Boots all dependent libraries. The dependencies must be initialized properly before the booting of this library or
   * application can start. If any of the dependencies fails to initialize properly, the whole boot-process will be
   * aborted.
   *
   * @param childs the array of dependencies, never null.
   */
  private void performBootDependencies( final DependencyInformation[] childs ) {
    if ( childs == null ) {
      return;
    }

    for ( int i = 0; i < childs.length; i++ ) {
      final DependencyInformation child = childs[ i ];
      if ( child instanceof ProjectInformation == false ) {
        continue;
      }
      final ProjectInformation projectInformation = (ProjectInformation) child;
      final AbstractBoot boot = loadBooter( projectInformation.getBootClass() );
      if ( boot != null ) {
        // but we're waiting until the booting is complete ...
        synchronized( boot ) {
          boot.start();
          while ( boot.isBootDone() == false &&
            boot.isBootFailed() == false ) {
            try {
              boot.wait();
            } catch ( InterruptedException e ) {
              // ignore it ..
            }
          }

          if ( boot.isBootFailed() ) {
            this.bootFailed = boot.getBootFailureReason();
            LOGGER.error( "Dependent project failed to boot up: " +
              projectInformation.getBootClass() + " failed to boot: ", this.bootFailed );
            return;
          }
        }
      }
    }
  }

  /**
   * Checks whether the booting failed. If booting failed, the reason for the failure (the Exception that caused the
   * error) is stored as property <code>bootFailureReason</code>.
   *
   * @return true, if booting failed, false otherwise.
   */
  public boolean isBootFailed() {
    return this.bootFailed != null;
  }

  /**
   * Returns the failure reason for the boot process. This method returns null, if booting was successful.
   *
   * @return the failure reason.
   */
  public Exception getBootFailureReason() {
    return bootFailed;
  }

  /**
   * Performs the boot.
   */
  protected abstract void performBoot();

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected abstract ProjectInformation getProjectInfo();

  /**
   * Loads the specified booter implementation.
   *
   * @param classname the class name.
   * @return The boot class.
   */
  protected AbstractBoot loadBooter( final String classname ) {
    return loadBooter( classname, getClass() );
  }

  /**
   * Loads the specified booter-class.
   *
   * @param classname the classname of the booter class.
   * @param source    the source-class from where to get the classloader.
   * @return the instantiated booter or null, if no booter could be loaded.
   */
  public static AbstractBoot loadBooter( final String classname, final Class source ) {
    if ( classname == null ) {
      return null;
    }
    if ( source == null ) {
      throw new NullPointerException();
    }
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( source );
      final Class c = Class.forName( classname, false, loader );
      final Method m = c.getMethod( "getInstance", (Class[]) null );
      return (AbstractBoot) m.invoke( null, (Object[]) null );
    } catch ( Exception e ) {
      LOGGER.info( "Unable to boot dependent class: " + classname );
      return null;
    }
  }

  /**
   * Creates a default configuration setup, which loads its settings from the static configuration (defaults provided by
   * the developers of the library) and the user configuration (settings provided by the deployer). The deployer's
   * settings override the developer's settings.
   * <p/>
   * If the parameter <code>addSysProps</code> is set to true, the system properties will be added as third
   * configuration layer. The system properties configuration allows to override all other settings.
   *
   * @param staticConfig the resource name of the developers configuration
   * @param userConfig   the resource name of the deployers configuration
   * @param addSysProps  a flag defining whether to include the system properties into the configuration.
   * @param source       the classloader source to load resources from.
   * @return the configured Configuration instance.
   */
  protected HierarchicalConfiguration createDefaultHierarchicalConfiguration
  ( final String staticConfig,
    final String userConfig,
    final boolean addSysProps,
    final Class source ) {
    if ( source == null ) {
      throw new NullPointerException( "SourceClass must not be null." );
    }

    final HierarchicalConfiguration globalConfig = new HierarchicalConfiguration( getClass() );

    if ( staticConfig != null ) {
      final PropertyFileConfiguration rootProperty = new PropertyFileConfiguration();
      rootProperty.load( staticConfig, source );
      globalConfig.insertConfiguration( rootProperty );
      globalConfig.insertConfiguration( getPackageManager().getPackageConfiguration() );
    }

    if ( userConfig != null ) {
      final String userConfigStripped;
      if ( userConfig.charAt( 0 ) == '/' ) {
        userConfigStripped = userConfig.substring( 1 );
      } else {
        userConfigStripped = userConfig;
      }

      try {
        final Enumeration userConfigs = ObjectUtilities.getClassLoader( source ).getResources( userConfigStripped );
        final ArrayList<PropertyFileConfiguration> configs = new ArrayList<PropertyFileConfiguration>();
        while ( userConfigs.hasMoreElements() ) {
          final URL url = (URL) userConfigs.nextElement();
          try {
            final PropertyFileConfiguration baseProperty = new PropertyFileConfiguration();
            final InputStream in = url.openStream();
            try {
              baseProperty.load( in );
            } finally {
              in.close();
            }
            configs.add( baseProperty );
          } catch ( IOException ioe ) {
            LOGGER.warn( "Failed to load the user configuration at " + url, ioe );
          }
        }

        final PropertyFileConfiguration compressedUserConfig = new PropertyFileConfiguration();
        for ( int i = configs.size() - 1; i >= 0; i-- ) {
          final PropertyFileConfiguration baseProperty = configs.get( i );
          compressedUserConfig.addAll( baseProperty );
        }
        globalConfig.insertConfiguration( compressedUserConfig );
      } catch ( IOException e ) {
        LOGGER.warn( "Failed to lookup the user configurations.", e );
      }
    }
    if ( addSysProps ) {
      final SystemPropertyConfiguration systemConfig = new SystemPropertyConfiguration();
      globalConfig.insertConfiguration( systemConfig );
    }
    return globalConfig;
  }

  /**
   * Returns the global configuration as extended configuration.
   *
   * @return the extended configuration.
   */
  public synchronized ExtendedConfiguration getExtendedConfig() {
    if ( extWrapper == null ) {
      extWrapper = new ExtendedConfigurationWrapper( getGlobalConfig() );
    }
    return extWrapper;
  }

  public synchronized ObjectFactory getObjectFactory() {
    try {
      if ( objectFactory == null ) {
        final String configProperty = getGlobalConfig().getConfigProperty( ObjectFactoryBuilder.class.getName(),
          DefaultObjectFactoryBuilder.class.getName() );
        final ObjectFactoryBuilder objectFactoryBuilder =
          ObjectUtilities.loadAndInstantiate( configProperty, getClass(), ObjectFactoryBuilder.class );
        objectFactory = objectFactoryBuilder.createObjectFactory( this );
      }
      return objectFactory;
    } catch ( Throwable t ) {
      throw new IllegalStateException( "ObjectFactory is not configured properly", t );
    }
  }
}
