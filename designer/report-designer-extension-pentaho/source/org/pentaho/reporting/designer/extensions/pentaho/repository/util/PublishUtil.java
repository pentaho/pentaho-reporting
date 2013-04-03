package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.pensol.PentahoSolutionsFileSystemConfigBuilder;
import org.pentaho.reporting.libraries.pensol.sugar.PublishRestUtil;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public class PublishUtil
{
  private static final String WEB_SOLUTION_PREFIX = "web-solution:";
  private static final String JCR_SOLUTION_PREFIX = "jcr-solution:";
  public static final String SERVER_VERSION = "server-version";
  public static final int SERVER_VERSION_SUGAR = 5;
  public static final int SERVER_VERSION_LEGACY = 4;
   private static final String SLASH = "/";
+  private static final String VIEWER = "/viewer";
+  private static final String COLON_SEP = ":";

  private PublishUtil()
  {

  }

  public static ReportRenderContext openReport(final ReportDesignerContext context,
                                               final AuthenticationData loginData,
                                               final String path)
      throws IOException, ReportDataFactoryException, ResourceException
  {
    if (StringUtils.isEmpty(path))
    {
      throw new IOException("Path is empty.");
    }

    final FileObject connection = createVFSConnection(loginData);
    final FileObject object = connection.resolveFile(path);
    if (object.exists() == false)
    {
      throw new FileNotFoundException(path);
    }

    final InputStream inputStream = object.getContent().getInputStream();
    try
    {
      final ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(8192, (int) object.getContent().getSize()));
      IOUtils.getInstance().copyStreams(inputStream, out);
      final MasterReport report = loadReport(out.toByteArray(), path);
      final int index = context.addMasterReport(report);
      return context.getReportRenderContext(index);
    }
    finally
    {
      inputStream.close();
    }
  }

  private static MasterReport loadReport(final byte[] data, final String fileName) throws
      IOException, ResourceException
  {
    if (data == null)
    {
      throw new NullPointerException();
    }
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();

    final MasterReport resource = OpenReportAction.loadReport(data, resourceManager);
    resource.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path", fileName); // NON-NLS
    return resource;
  }

  public static void launchReportOnServer(final String baseUrl, final String path) throws IOException
  {

    if (StringUtils.isEmpty(path))
    {
      throw new IOException("Path is empty.");
    }

    final String[] pathElements = StringUtils.split(path, "/");
    if (pathElements.length < 2)
    {
      throw new IOException("Path is invalid.");
    }

    final int lastElementIndex = pathElements.length - 1;

    final String solution = pathElements[0];
    final String filename = pathElements[lastElementIndex];
    final StringBuilder contentPath = new StringBuilder();
    for (int i = 1; i < lastElementIndex; i++)
    {
      contentPath.append('/');
      contentPath.append(pathElements[i]);
    }


    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String urlMessage = config.getConfigProperty
        ("org.pentaho.reporting.designer.extensions.pentaho.repository.LaunchReport");   
+   final String fullpath = COLON_SEP+ solution.replaceAll(SLASH,COLON_SEP) +
+        contentPath.toString().replaceAll(SLASH,COLON_SEP) + COLON_SEP + filename;
+   final String url = (baseUrl + urlMessage + fullpath).replaceAll(" ","%20")+VIEWER;
    ExternalToolLauncher.openURL(url);
  }

  public static byte[] createBundleData(final MasterReport report) throws PublishException, BundleWriterException
  {
    try
    {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BundleWriter.writeReportToZipStream(report, outputStream);
      return outputStream.toByteArray();
    }
    catch (final ContentIOException e)
    {
      throw new BundleWriterException("Failed to write report", e);
    }
    catch (final IOException e)
    {
      throw new BundleWriterException("Failed to write report", e);
    }
  }

  public static void publish(final byte[] data,
                             final String path,
                             final AuthenticationData loginData)
      throws IOException
  {
	  
	final String versionText = loginData.getOption(SERVER_VERSION);
	final int version = ParserUtil.parseInt(versionText, SERVER_VERSION_SUGAR);  

	if (SERVER_VERSION_SUGAR == version){
			
		new PublishRestUtil(loginData.getUrl(), loginData.getUsername(), loginData.getPassword()).publishFile(path, data, true);
		     
	}else {
  

    final FileObject connection = createVFSConnection(loginData);
    final FileObject object = connection.resolveFile(path);
    final OutputStream out = object.getContent().getOutputStream(false);
    try
    {
      out.write(data);
    }
    finally
    {
      out.close();
    }
	}
  }

  public static boolean acceptFilter(final String[] filters, final String name)
  {
    if (filters == null || filters.length == 0)
    {
      return true;
    }
    for (int i = 0; i < filters.length; i++)
    {
      if (name.endsWith(filters[i]))
      {
        return true;
      }
    }
    return false;
  }

  public static FileObject createVFSConnection(final AuthenticationData loginData) throws FileSystemException
  {
    return createVFSConnection(VFS.getManager(), loginData);
  }

  public static FileObject createVFSConnection(final FileSystemManager fileSystemManager,
                                               final AuthenticationData loginData) throws FileSystemException
  {
    if (fileSystemManager == null)
    {
      throw new NullPointerException();
    }
    if (loginData == null)
    {
      throw new NullPointerException();
    }

    final String versionText = loginData.getOption(SERVER_VERSION);
    final int version = ParserUtil.parseInt(versionText, SERVER_VERSION_SUGAR);

    final String normalizedUrl = normalizeURL(loginData.getUrl(), version);
    final FileSystemOptions fileSystemOptions = new FileSystemOptions();
    final PentahoSolutionsFileSystemConfigBuilder configBuilder = new PentahoSolutionsFileSystemConfigBuilder();
    configBuilder.setTimeOut(fileSystemOptions, getTimeout(loginData) * 1000);
    configBuilder.setUserAuthenticator(fileSystemOptions, new StaticUserAuthenticator(normalizedUrl,
        loginData.getUsername(), loginData.getPassword()));
    return fileSystemManager.resolveFile(normalizedUrl, fileSystemOptions);
  }

  public static int getTimeout(final AuthenticationData loginData)
  {
    final String s = loginData.getOption("timeout");
    return ParserUtil.parseInt(s, WorkspaceSettings.getInstance().getConnectionTimeout());
  }

  public static String normalizeURL(final String baseURL, final int version)
  {
    if (baseURL == null)
    {
      throw new NullPointerException();
    }
    final StringBuilder prefix = new StringBuilder(100);
    final String url2;
    if (version == SERVER_VERSION_LEGACY)
    {
      if (baseURL.toLowerCase(Locale.ENGLISH).startsWith("http://")) // NON-NLS
      {
        url2 = baseURL.substring("http://".length());// NON-NLS
        prefix.append(WEB_SOLUTION_PREFIX);
        prefix.append("http://");// NON-NLS
      }
      else if (baseURL.toLowerCase(Locale.ENGLISH).startsWith("https://"))// NON-NLS
      {
        url2 = baseURL.substring("https://".length());// NON-NLS
        prefix.append(WEB_SOLUTION_PREFIX);
        prefix.append("https://");// NON-NLS
      }
      else
      {
        throw new IllegalArgumentException("Not a expected URL");
      }
    }
    else
    {
      if (baseURL.toLowerCase(Locale.ENGLISH).startsWith("http://")) // NON-NLS
      {
        url2 = baseURL.substring("http://".length());// NON-NLS
        prefix.append(JCR_SOLUTION_PREFIX);
        prefix.append("http://");// NON-NLS
      }
      else if (baseURL.toLowerCase(Locale.ENGLISH).startsWith("https://"))// NON-NLS
      {
        url2 = baseURL.substring("https://".length());// NON-NLS
        prefix.append(JCR_SOLUTION_PREFIX);
        prefix.append("https://");// NON-NLS
      }
      else
      {
        throw new IllegalArgumentException("Not a expected URL");
      }
    }
    return prefix.append(url2).toString();
  }
}
