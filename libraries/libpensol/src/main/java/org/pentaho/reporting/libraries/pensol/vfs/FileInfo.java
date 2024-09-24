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

package org.pentaho.reporting.libraries.pensol.vfs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class FileInfo {
  private boolean directory;
  private boolean visible;
  private String name;
  private String localizedName;
  private String description;
  private long lastModifiedDate;
  private String parameterServiceURL;
  private String title;
  private String url;
  private FileInfo parent;
  private ArrayList<FileInfo> childs;

  public FileInfo() {
    directory = true;
    visible = true;
    name = "";
    localizedName = "";
    description = "Content Root";
    lastModifiedDate = System.currentTimeMillis();
    childs = new ArrayList<FileInfo>();
  }

  public FileInfo( final FileInfo parent, final String name, final String description ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }

    if ( name == null ) {
      throw new NullPointerException();
    }
    directory = true;
    visible = true;
    this.name = name;
    localizedName = "";
    this.description = description;
    lastModifiedDate = System.currentTimeMillis();
    childs = new ArrayList<FileInfo>();
    this.parent = parent;
    this.parent.childs.add( this );
  }

  public FileInfo( final FileInfo parent, final Attributes element ) throws SAXException {
    if ( parent == null ) {
      throw new SAXException();
    }
    if ( element == null ) {
      throw new SAXException();
    }

    name = element.getValue( "name" );
    if ( name == null ) {
      throw new IllegalStateException
        ( "<name> attribute is null. Your BI-Server serves incorrect solution-repository files." );
    }
    localizedName = element.getValue( "localized-name" );
    if ( localizedName == null ) {
      localizedName = name;
    }

    directory = "true".equals( element.getValue( "isDirectory" ) );
    visible = "true".equals( element.getValue( "visible" ) );
    final String lastModifiedRaw = element.getValue( "lastModifiedDate" );
    if ( lastModifiedRaw != null ) {
      try {
        lastModifiedDate = Long.parseLong( lastModifiedRaw );
      } catch ( final NumberFormatException nfe ) {
        throw new SAXException();
      }
    }
    description = element.getValue( "description" );
    title = element.getValue( "title" );
    if ( title == null ) {
      title = element.getValue( "url_name" );
    }

    url = element.getValue( "url" );
    parameterServiceURL = element.getValue( "param-service-url" );

    childs = new ArrayList<FileInfo>();
    this.parent = parent;
    this.parent.childs.add( this );
  }

  public boolean isDirectory() {
    return directory;
  }

  public boolean isVisible() {
    return visible;
  }

  public String getName() {
    return name;
  }

  public String getLocalizedName() {
    return localizedName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( final String description ) {
    this.description = description;
  }

  public long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public String getParameterServiceURL() {
    return parameterServiceURL;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public FileInfo getParent() {
    return parent;
  }

  public FileInfo[] getChilds() {
    return childs.toArray( new FileInfo[ childs.size() ] );
  }

  public FileInfo getChild( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    for ( int i = 0; i < childs.size(); i++ ) {
      final FileInfo fileInfo = childs.get( i );
      if ( name.equals( fileInfo.getName() ) ) {
        return fileInfo;
      }
    }
    return null;
  }
}
