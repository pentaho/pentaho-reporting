/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.repository;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

import java.util.zip.ZipOutputStream;


/**
 * The LibRepositoryBoot class is used to initialize the library before it is first used. This loads all configurations
 * and initializes all factories.
 * <p/>
 * Without booting, basic services like logging and the global configuration will not be availble.
 *
 * @author Thomas Morgner
 */
public class LibRepositoryBoot extends AbstractBoot {
  /**
   * A attribute domain name for managing ZIP-Attributes.
   */
  public static final String ZIP_DOMAIN = "org.jfree.repository.zip";
  /**
   * A attribute name representing a ZIP compression method.
   */
  public static final String ZIP_METHOD_ATTRIBUTE = "method";
  /**
   * A attribute name representing a ZIP attribute value.
   */
  public static final Integer ZIP_METHOD_STORED = new Integer( ZipOutputStream.STORED );
  /**
   * A attribute name representing a ZIP attribute value.
   */
  public static final Integer ZIP_METHOD_DEFLATED = new Integer( ZipOutputStream.DEFLATED );

  /**
   * A attribute name representing a ZIP compression level.
   */
  public static final String ZIP_COMPRESSION_ATTRIBUTE = "compression";
  /**
   * A attribute name representing a ZIP entry comment.
   */
  public static final String ZIP_COMMENT_ATTRIBUTE = "comment";
  /**
   * A attribute name representing a ZIP attribute.
   */
  public static final String ZIP_CRC32_ATTRIBUTE = "crc32";

  /**
   * A attribute domain name for managing general attributes.
   */
  public static final String REPOSITORY_DOMAIN = "org.jfree.repository";
  /**
   * A attribute name representing the content-item size. This should always return a Number.
   */
  public static final String SIZE_ATTRIBUTE = "size";

  /**
   * A attribute name representing the content-entity versioning information. The object used as versioning information
   * is implementation-specific and should only be used to compare equality.
   */
  public static final String VERSION_ATTRIBUTE = "version";
  /**
   * A attribute name representing the content-entities mime-type information (if stored in the repository).
   */
  public static final String CONTENT_TYPE = "content-type";

  private static LibRepositoryBoot instance;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibRepositoryBoot getInstance() {
    if ( instance == null ) {
      instance = new LibRepositoryBoot();
    }
    return instance;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibRepositoryBoot() {
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/repository/librepository.properties",
        "/librepository.properties", true, LibRepositoryBoot.class );

  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibRepositoryInfo.getInstance();
  }
}
