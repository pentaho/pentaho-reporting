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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pensol;

import java.util.List;
import java.util.ArrayList;
import java.util.WeakHashMap;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class CookiesHandlerFilter extends ClientFilter {
	private static final Log logger = LogFactory.getLog(CookiesHandlerFilter.class);

	// Use WeakHashMap as cache. It's not synchronized, so use "class"
	// synchronized get/setters.
	private static WeakHashMap<String, List<Object>> cookiesByAuth = new WeakHashMap<String, List<Object>>();

	// "class" synchronized get/setters:
	private static synchronized List<Object> getCachedCookiesByAuthToken(String authToken) {
		return cookiesByAuth.get(authToken);
	}

	private static synchronized void setCookiesByAuthToken(String authToken, List<Object> cookiesList) {
		cookiesByAuth.put(authToken, cookiesList);
	}

	@Override
	public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
		List<Object> authorizationHeader = request.getHeaders().get("Authorization");
		String authToken = (String) authorizationHeader.get(0);
		// If we have cookies, inject them. When session is expired, basic auth
		// header is not used instead of the expired cookies, so we just use
		// them as a token.
		List<Object> cookiesList = getCachedCookiesByAuthToken(authToken);
		if (cookiesList != null) {
			request.getHeaders().put("Cookie", cookiesList);
			request.getHeaders().remove("Authorization");
		}
		ClientResponse response = getNext().handle(request);
		// If session has expired, remove cookies, put back basic auth header,
		// try one more time and save new cookies.
		if (response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
			logger.warn("Request to" + request.getURI() + "returned Unauthorized.");
			if (logger.isDebugEnabled()) {
				logger.debug("http status=" + response.getStatus() + " response=" + response.getEntity(String.class));
			}
			request.getHeaders().remove("Cookie");
			request.getHeaders().put("Authorization", authorizationHeader);
			logger.warn("Trying one more time");
			response = getNext().handle(request);
			if (response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
				logger.error("Request to" + request.getURI() + "returned Unauthorized 2nd time.");
				logger.error("http status=" + response.getStatus() + " response=" + response.getEntity(String.class));
			}
		}
		// always use the new cookies.
		if (response.getCookies() != null && response.getCookies().isEmpty() == false) {
			cookiesList = new ArrayList<Object>();
			cookiesList.addAll(response.getCookies());
			setCookiesByAuthToken(authToken, cookiesList);
		}
		return response;
	}
}
