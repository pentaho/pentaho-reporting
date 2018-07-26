/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2018 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.libraries.pensol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;

/**
 * This class is a proxy for {@linkplain RepositoryFileTreeDto} needed to avoid loading full files' tree during the only
 * request. It follows this contract: if <code>RepositoryFileTreeDto.getChildren()</code> returns <code>null</code>,
 * then it is considered to be not initialised.
 *
 * @author Andrey Khayrutdinov
 */
public class RepositoryFileTreeDtoProxy extends RepositoryFileTreeDto {
  private static final String URL_TEMPLATE = "/api/repo/files/:REPLACEMENT:/tree?depth=1&filter=*&showHidden=true";

  private final RepositoryFileTreeDto dto;
  private final Client client;
  private final String baseUrl;

  public RepositoryFileTreeDtoProxy( RepositoryFileTreeDto dto, Client client, String baseUrl ) {
    this.dto = dto;
    this.baseUrl = baseUrl;
    this.client = client;

    if ( this.dto.getFile().isFolder() ) {
      this.dto.setChildren( null );
    } else {
      this.dto.setChildren( Collections.<RepositoryFileTreeDto>emptyList() );
    }
  }

  public List<RepositoryFileTreeDto> getChildren() {
    List<RepositoryFileTreeDto> children = dto.getChildren();
    if ( children == null ) {
      synchronized ( this ) {
        children = dto.getChildren();
        if ( children == null ) {
          children = loadChildren( dto.getFile().getPath() );
          dto.setChildren( children );
        }
      }
    }
    return children;
  }

  private List<RepositoryFileTreeDto> loadChildren( String path ) {
    String encodedPath = JCRSolutionFileModel.encodePathForRequest( path );
    String childrenUrl = URL_TEMPLATE.replace( ":REPLACEMENT:", encodedPath );
    WebResource resource = client.resource( baseUrl + childrenUrl );

    RepositoryFileTreeDto element =
      resource.path( "" ).accept( MediaType.APPLICATION_XML_TYPE ).get( RepositoryFileTreeDto.class );
    List<RepositoryFileTreeDto> tree;
    if ( element == null || element.getChildren() == null ) {
      tree = Collections.emptyList();
    } else {
      List<RepositoryFileTreeDto> children = element.getChildren();
      tree = new ArrayList<RepositoryFileTreeDto>( children.size() );
      for ( RepositoryFileTreeDto child : children ) {
        RepositoryFileTreeDtoProxy dto = new RepositoryFileTreeDtoProxy( child, client, baseUrl );
        tree.add( dto );
      }
    }
    return tree;
  }

  public RepositoryFileDto getFile() {
    return dto.getFile();
  }

  public void setFile( final RepositoryFileDto file ) {
    dto.setFile( file );
  }

  public void setChildren( final List<RepositoryFileTreeDto> children ) {
    dto.setChildren( children );
  }

  public RepositoryFileTreeDto getRealObject() {
    return dto;
  }
}
