package org.pentaho.reporting.libraries.pensol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.reporting.libraries.base.util.FastStack;

public class JCRSolutionFileModel implements SolutionFileModel
{
  private static final Log logger = LogFactory.getLog(JCRSolutionFileModel.class);

  private static final String LOAD_REPOSITORY_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.LoadRepositoryDoc");
  private static final String CREATE_FOLDER_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.CreateNewFolder");
  private static final String DOWNLOAD_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.DownloadService");
  private static final String UPLOAD_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.UploadService");
  public static final String RETRIEVE_CONTENT_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.RetrieveContent");
  public static final String RETRIEVE_PARAMETER_URL_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty
      ("org.pentaho.reporting.libraries.pensol.jcr.RetrieveParameters");

  private static final String BI_SERVER_NULL_OBJECT = "BI-Server returned a RepositoryFileTreeDto without an attached RepositoryFileDto. " +
      "Please file a bug report at http://jira.pentaho.org/browse/BISERVER !";
  private static final String FILE_NOT_FOUND = "The specified file name does not exist: {0}";

  private Client client;
  private String url;
  private RepositoryFileTreeDto root;
  private HashMap<FileName, String> descriptionEntries;
  private long refreshTime;
  private String majorVersion;
  private String minorVersion;
  private String releaseVersion;
  private String buildVersion;
  private String milestoneVersion;

  public JCRSolutionFileModel(final String url,
                              final String username,
                              final String password,
                              final int timeout)
  {
    if (url == null)
    {
      throw new NullPointerException();
    }
    this.url = url;
    descriptionEntries = new HashMap<FileName, String>();

    final ClientConfig config = new DefaultClientConfig();
    config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
    config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
    this.client = Client.create(config);
    this.client.addFilter(new HTTPBasicAuthFilter(username, password));
    this.majorVersion = "999";
    this.minorVersion = "999";
    this.releaseVersion = "999";
    this.buildVersion = "999";
    this.milestoneVersion = "999";
  }

  public void refresh() throws IOException
  {
    final WebResource resource = client.resource(url + LOAD_REPOSITORY_SERVICE);
    final RepositoryFileTreeDto tree = resource.path("").accept(MediaType.APPLICATION_XML_TYPE).get(RepositoryFileTreeDto.class);
    setRoot(tree);
  }

  public void createFolder(final FileName file) throws FileSystemException
  {
    try
    {
      final String path = normalizePath(file.getPath());
      final String service = MessageFormat.format(CREATE_FOLDER_SERVICE, path);

      final WebResource resource = client.resource(url + service);
      final ClientResponse response = resource.type("text/plain").put(ClientResponse.class, null);
      if (response.getStatus() == 200)
      {
        refresh();
      }
      else
      {
        throw new FileSystemException("Failed with error-code", response.getStatus());
      }
    }
    catch (FileSystemException fse)
    {
      throw fse;
    }
    catch (Exception e)
    {
      throw new FileSystemException("Failed", e);
    }
  }

  private static String normalizePath(final String path)
  {
    return path.replace("/", ":");
  }

  public RepositoryFileTreeDto getRoot() throws IOException
  {
    if (root == null && refreshTime == 0)
    {
      refresh();
    }

    return root;
  }

  public void setRoot(final RepositoryFileTreeDto root)
  {
    if (root == null)
    {
      throw new NullPointerException();
    }

    this.descriptionEntries.clear();
    this.root = root;
    this.refreshTime = System.currentTimeMillis();
  }

  public boolean isDirectory(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    return fileDto.isFolder();
  }

  public boolean exists(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    return (fileInfo != null);
  }

  public boolean isVisible(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    return !fileDto.isHidden();
  }

  public void setDescription(final FileName file, final String description) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    fileDto.setDescription(description);
  }

  public String getDescription(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    return fileDto.getDescription();
  }

  public long getLastModifiedDate(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    final Date lastModifiedDate = fileDto.getLastModifiedDate();
    if (lastModifiedDate == null)
    {
      logger.error("Repository returned <null> for last-modified-date on file: " + file);
      return -1;
    }
    return lastModifiedDate.getTime();
  }

  private String getFormattedServiceUrl(final String urlService, final FileName file) throws FileSystemException
  {
    if (urlService == null)
    {
      throw new NullPointerException();
    }
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }

    final String restName = normalizePath(file.getPath());
    return MessageFormat.format(urlService, restName);
  }

  public String getUrl(final FileName file) throws FileSystemException
  {
    return getFormattedServiceUrl(RETRIEVE_CONTENT_SERVICE, file);
  }

  public List<RepositoryFileTreeDto> getChildren(final FileName parent) throws FileSystemException
  {
    final String[] pathArray = computeFileNames(parent);
    final RepositoryFileTreeDto fileInfo = lookupNode(pathArray);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, parent);
    }

    final List<RepositoryFileTreeDto> childNodes = fileInfo.getChildren();
    return childNodes == null ? Collections.<RepositoryFileTreeDto>emptyList() : childNodes;
  }

  protected RepositoryFileTreeDto lookupNode(final String[] path) throws FileSystemException
  {
    if (root == null)
    {
      try
      {
        refresh();
      }
      catch (IOException e)
      {
        throw new FileSystemException(e);
      }
    }
    if (path.length == 0)
    {
      return root;
    }
    if ("".equals(path[0]))
    {
      if (path.length == 1)
      {
        return root;
      }
    }
    RepositoryFileTreeDto element = root;
    for (final String pathSegment : path)
    {
      RepositoryFileTreeDto name = null;
      final List<RepositoryFileTreeDto> children = element.getChildren();
      if (children == null)
      {
        return null;
      }

      for (final RepositoryFileTreeDto child : children)
      {
        final RepositoryFileDto file = child.getFile();
        if (file == null)
        {
          throw new FileSystemException(BI_SERVER_NULL_OBJECT);
        }
        if (pathSegment.equals(file.getName()))
        {
          name = child;
          break;
        }
      }
      if (name == null)
      {
        return null;
      }
      element = name;
    }
    return element;
  }

  protected String[] computeFileNames(FileName file)
  {
    final FastStack<String> stack = new FastStack<String>();
    while (file != null)
    {
      final String name = file.getBaseName().trim();
      if (!"".equals(name))
      {
        stack.push(name);
      }
      file = file.getParent();
    }

    final int size = stack.size();
    final String[] result = new String[size];
    for (int i = 0; i < result.length; i++)
    {
      result[i] = stack.pop();
    }
    return result;
  }

  public byte[] getData(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }

    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new IllegalStateException(BI_SERVER_NULL_OBJECT);
    }
    final String path = normalizePath(fileDto.getPath());
    final String service = MessageFormat.format(DOWNLOAD_SERVICE, path);
    return client.resource(url + service).accept(MediaType.APPLICATION_XML_TYPE).get(byte[].class);
  }

  public void setData(final FileName file, final byte[] data) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final StringBuilder b = new StringBuilder();
    for (int i = 0; i < fileName.length; i++)
    {
      if (i != 0)
      {
        b.append("/");
      }
      b.append(fileName[i]);
    }

    final String service = MessageFormat.format(UPLOAD_SERVICE, normalizePath(b.toString()));
    final WebResource resource = client.resource(url + service);
    final ByteArrayInputStream stream = new ByteArrayInputStream(data);
    final ClientResponse response = resource.put(ClientResponse.class, stream);
    final int status = response.getStatus();

    if (status != HttpStatus.SC_OK)
    {
      if (status == HttpStatus.SC_MOVED_TEMPORARILY ||
          status == HttpStatus.SC_FORBIDDEN ||
          status == HttpStatus.SC_UNAUTHORIZED)
      {
        throw new FileSystemException("ERROR_INVALID_USERNAME_OR_PASSWORD");
      }
      else
      {
        throw new FileSystemException("ERROR_FAILED", status);
      }
    }
  }

  public String[] getChilds(final FileName name) throws FileSystemException
  {
    final List<RepositoryFileTreeDto> children = getChildren(name);
    final String[] childrenArray = new String[children.size()];
    for (int i = 0; i < children.size(); i++)
    {
      final RepositoryFileTreeDto repositoryFileTreeDto = children.get(i);
      if (repositoryFileTreeDto == null)
      {
        continue;
      }

      final RepositoryFileDto file = repositoryFileTreeDto.getFile();
      if (file == null)
      {
        throw new FileSystemException(BI_SERVER_NULL_OBJECT);
      }
      childrenArray[i] = file.getName();
    }
    return childrenArray;
  }

  public String getLocalizedName(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);
    final RepositoryFileTreeDto fileInfo = lookupNode(fileName);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, file);
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if (fileDto == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    return fileDto.getTitle();
  }

  public String getParamServiceUrl(final FileName name) throws FileSystemException
  {
    return url + getFormattedServiceUrl(RETRIEVE_PARAMETER_URL_SERVICE, name);
  }

  public long getRefreshTime()
  {
    return refreshTime;
  }

  public void setRefreshTime(final long refreshTime)
  {
    this.refreshTime = refreshTime;
  }

  public HashMap<FileName, String> getDescriptionEntries()
  {
    return descriptionEntries;
  }

  public String getMajorVersion()
  {
    return majorVersion;
  }

  public String getMinorVersion()
  {
    return minorVersion;
  }

  public String getReleaseVersion()
  {
    return releaseVersion;
  }

  public String getBuildVersion()
  {
    return buildVersion;
  }

  public String getMilestoneVersion()
  {
    return milestoneVersion;
  }

  public long getContentSize(final FileName name) throws FileSystemException
  {
    final String[] pathArray = computeFileNames(name);
    final RepositoryFileTreeDto fileInfo = lookupNode(pathArray);
    if (fileInfo == null)
    {
      throw new FileSystemException(FILE_NOT_FOUND, name);
    }
    final RepositoryFileDto file = fileInfo.getFile();
    if (file == null)
    {
      throw new FileSystemException(BI_SERVER_NULL_OBJECT);
    }
    return file.getFileSize();
  }
}
