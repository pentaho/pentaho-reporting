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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.swingicons;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.action.AbstractFileSelectionAction;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demonstration application. <P> This demo is written up in the JFreeReport PDF Documentation.  Please notify David
 * Gilbert (david.gilbert@object-refinery.com) if you need to make changes to this file. <P> To run this demo, you need
 * to have the Java Look and Feel Icons jar file on your classpath.
 *
 * @author David Gilbert
 */
public class SwingIconsDemoPanel extends JPanel
{
  private static final Log logger = LogFactory.getLog(SwingIconsDemoPanel.class);

  private class SelectRepositoryFileAction extends AbstractFileSelectionAction
  {
    private File selectedFile;

    protected SelectRepositoryFileAction()
    {
      super(SwingIconsDemoPanel.this);
      putValue(Action.NAME, "Select graphics archive ..");
      this.putValue(Action.SMALL_ICON, ImageUtils.createTransparentIcon(16, 16));
      this.putValue("ICON24", ImageUtils.createTransparentIcon(24, 24));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      selectedFile = performSelectFile(selectedFile, JFileChooser.OPEN_DIALOG, true);
      if (selectedFile != null)
      {
        if (selectedFile.exists() && selectedFile.canRead() && selectedFile.isFile())
        {
          try
          {
            loadData(selectedFile.toURI().toURL());
          }
          catch (MalformedURLException ex)
          {
            logger.warn("Unable to form local file URL. Is there no local filesystem?");
          }
        }
      }
    }

    /**
     * Returns a descriptive text describing the file extension.
     *
     * @return the file description.
     */
    protected String getFileDescription()
    {
      return "Java Look and Feel Graphics Repository";
    }

    /**
     * Returns the file extension that should be used for the operation.
     *
     * @return the file extension.
     */
    protected String getFileExtension()
    {
      return ".jar";
    }
  }

  /**
   * The data for the report.
   */
  private SwingIconsDemoTableModel data;

  /**
   * Constructs the demo application.
   */
  public SwingIconsDemoPanel()
  {
    data = new SwingIconsDemoTableModel();

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    final JTable table = new JTable(data);
    table.setDefaultRenderer(Image.class, new ImageCellRenderer());
    table.setRowHeight(26);
    final JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, BorderLayout.CENTER);

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
    buttonPanel.add(new JButton(new SelectRepositoryFileAction()));
    add(buttonPanel, BorderLayout.SOUTH);

    loadData(findDataFile());
  }

  protected void loadData(final URL sourceURL)
  {
    if (sourceURL != null)
    {
      // on success update the config path, else clear the path.
      if (this.data.readData(sourceURL))
      {
        storeToConfiguration(sourceURL);
        return;
      }
      else
      {
        final String message =
            ("There was a problem while loading 'jlfgr-1_0.jar'.\n"
                + "A URL was given, but the contents seems to be invalid.\n\n"
                + "You may download this jar-file from: \n"
                + "http://java.sun.com/developer/techDocs/hi/repository/");
        logger.warn(message);
      }
    }
    this.data.clear();
    storeToConfiguration(null);
  }

  /**
   * Loads the URL of the Graphics Repository from the local configuration.
   *
   * @return the loaded URL or null, if the configuration did not hold an entry.
   */
  protected URL loadFromConfiguration()
  {
    final String configPath = ConfigFactory.encodePath("SwingIconsDemo-TableModel");
    final ConfigStorage cs = ConfigFactory.getInstance().getUserStorage();
    if (cs.isAvailable(configPath) == false)
    {
      return null;
    }
    try
    {
      final Configuration p = cs.load(configPath, null);
      final String property = p.getConfigProperty("repository-path");
      if (property == null)
      {
        return null;
      }
      return new URL(property);
    }
    catch (Exception e)
    {
      return null;
    }
  }

  protected void storeToConfiguration(final URL url)
  {
    final String configPath = ConfigFactory.encodePath("SwingIconsDemo-TableModel");
    final ConfigStorage cs = ConfigFactory.getInstance().getUserStorage();
    try
    {
      final DefaultConfiguration p = new DefaultConfiguration();
      if (url != null)
      {
        p.setConfigProperty("repository-path", url.toExternalForm());
      }
      cs.store(configPath, p);
    }
    catch (Exception e)
    {
      // ignored ..
      logger.debug("Unable to store the configuration.", e);
    }
  }

  /**
   * Searches for the 'jlfgr_1_0.jar' file on the classpath, in the classpath directories and the working directory. If
   * that fails, the user is asked to choose the correct file.
   *
   * @return the URL to the graphics repository.
   */
  private URL findDataFile()
  {
    final URL url = ObjectUtilities.getResource("jlfgr-1_0.jar", SwingIconsDemoPanel.class);
    if (url != null)
    {
      return url;
    }
    final URL urlFromConfig = loadFromConfiguration();
    if (urlFromConfig != null)
    {
      return urlFromConfig;
    }

    final File localFile = new File("jlfgr-1_0.jar");
    if (localFile.exists() && localFile.canRead() && localFile.isFile())
    {
      try
      {
        return localFile.toURI().toURL();
      }
      catch (MalformedURLException e)
      {
        logger.warn("Unable to form local file URL. Is there no local filesystem?");
      }
    }

    final File classpathFile = findFileOnClassPath("jlfgr-1_0.jar");
    if (classpathFile != null)
    {
      if (classpathFile.exists() && classpathFile.canRead() && classpathFile.isFile())
      {
        try
        {
          return classpathFile.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
          logger.warn("Unable to form local file URL. Is there no local filesystem?");
        }
      }
    }

    if (warnedUser == false && GraphicsEnvironment.isHeadless() == false)
    {
      warnedUser = true;
      final String title = "Unable to load the icons.";
      final String message = ("Unable to find 'jlfgr-1_0.jar'\n"
          + "Please make sure you have the Java Look and Feel Graphics Repository in "
          + "in your classpath, the same directory as the JFreeReport-jar files or in "
          + "the current working directory.\n\n"
          + "You may download this jar-file from: \n"
          + "http://java.sun.com/developer/techDocs/hi/repository/");
      JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    return null;
  }

  private static boolean warnedUser;

  /**
   * Returns a reference to a file with the specified name that is located somewhere on the classpath.  The code for
   * this method is an adaptation of code supplied by Dave Postill.
   *
   * @param name the filename.
   * @return a reference to a file or <code>null</code> if no file could be found.
   */
  public static File findFileOnClassPath(final String name)
  {

    final String classpath = System.getProperty("java.class.path");
    final String pathSeparator = System.getProperty("path.separator");

    final StringTokenizer tokenizer = new StringTokenizer(classpath, pathSeparator);

    while (tokenizer.hasMoreTokens())
    {
      final String pathElement = tokenizer.nextToken();

      final File directoryOrJar = new File(pathElement);
      final File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();

      if (absoluteDirectoryOrJar.isFile())
      {
        final File target = new File(absoluteDirectoryOrJar.getParent(), name);
        if (target.exists())
        {
          return target;
        }
      }
      else
      {
        final File target = new File(directoryOrJar, name);
        if (target.exists())
        {
          return target;
        }
      }

    }
    return null;

  }

  public SwingIconsDemoTableModel getData()
  {
    return data;
  }
}
