/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */


package org.snipsnap.xmlrpc;

/**
 * Handles XML-RPC calls for the Wiki API
 * http://www.ecyrd.com/JSPWiki/Wiki.jsp?page=WikiRPCInterface
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class WikiHandler {
  /**
   *
   * array getRecentChanges( Date timestamp )
   * int getRPCVersionSupported(): Returns 1 with this version of the JSPWiki API.
   * base64 getPage( String pagename ): Get the raw Wiki text of page, latest version.
   * base64 getPageVersion( String pagename, int version ): Get the raw Wiki text of page. Returns UTF-8, expects UTF-8 with URL encoding.
   * base64 getPageHTML( String pagename ): Return page in rendered HTML. Returns UTF-8, expects UTF-8 with URL encoding.
   * base64 getPageHTMLVersion( String pagename, int version ): Return page in rendered HTML, UTF-8.
   * array getAllPages(): Returns a list of all pages. The result is an array of strings, again UTF-8 in URL encoding.
   * struct getPageInfo( string pagename ) : returns a struct with elements
   * struct getPageInfoVersion( string pagename, int version ) : returns a struct just like plain getPageInfo(), but this time for a specific version.
   * array listLinks( string pagename ): Lists all links for a given page. The returned array contains structs, with the following elements:
   **/
}
