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
package org.snipsnap.net;

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.feeder.Feeder;
import org.snipsnap.feeder.FeederRepository;
import org.snipsnap.render.PlainTextRenderEngine;
import org.snipsnap.semanticweb.rss.BlogFeeder;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.FeedException;


/**
 * Load a snip for output as RSS
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class RssServlet extends HttpServlet {
  // For date and time see http://www.w3.org/TR/NOTE-datetime
  private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private static SimpleDateFormat year = new SimpleDateFormat("yyyy");

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    SnipSpace space = SnipSpaceFactory.getInstance();
    Configuration config = Application.get().getConfiguration();

    String eTag = request.getHeader("If-None-Match");
    if (null != eTag && eTag.equals(space.getETag())) {
      response.setHeader("ETag", space.getETag());
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    } else {
      String version = request.getParameter("version");
      String type = request.getParameter("type");
      String sourceSnipName = request.getParameter("snip");

      if (null == sourceSnipName) {
        sourceSnipName = config.getStartSnip();
      }

      Snip sourceSnip = space.load(sourceSnipName);

      Object object = Components.getComponent(PlainTextRenderEngine.class);
      System.err.println("object = " + object.getClass());

      FeederRepository repository = (FeederRepository) Components.getComponent(FeederRepository.class);

      Feeder feeder = (Feeder) repository.get(type);

//      System.out.println("Feeder repository: "+repository.getPlugins());
      if (null == feeder || "blog".equals(feeder.getName())) {
        if (sourceSnip.isWeblog()) {
          feeder = new BlogFeeder(sourceSnipName);
        } else {
          feeder = new BlogFeeder();
        }
      }

      Snip snip = feeder.getContextSnip();

      SyndFeedI feed = new SyndFeed();
      // old snipsnaps supported 1.0 0.92 plain else=2.0
      // rome supports
      // rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3
      if ("rss_0.90".equals(version)) {
        feed.setFeedType("rss_0.90");
      } else if ("rss_0.91".equals(version)) {
        feed.setFeedType("rss_0.91");
      } else if ("0.92".equals(version) || "rss_0.92".equals(version)) {
        feed.setFeedType("rss_0.92");
      } else if ("rss_0.93".equals(version)) {
        feed.setFeedType("rss_0.93");
      } else if ("rss_0.94".equals(version)) {
        feed.setFeedType("rss_0.94");
      } else if ("1.0".equals(version) || "rss_1.0".equals(version)) {
        feed.setFeedType("rss_1.0");
      } else if ("rss_2.0".equals(version)) {
        feed.setFeedType("rss_2.0");
      } else if ("atom_0.3".equals(version)) {
        feed.setFeedType("atom_0.3");
      } else {
        feed.setFeedType("atom_0.3");
      }

      // feed.setEtag(SnipSpaceFactory.getInstance().getETag());
      String url = config.getUrl("/space");
      feed.setTitle(config.getName());
      feed.setLink(url + "/" + snip.getNameEncoded());
      feed.setDescription(config.getTagline());
      feed.setCopyright("Copyright "+year.format(snip.getModified().getmTime()));
      feed.setLanguage(config.getLocale().getLanguage());

      List entries = new ArrayList();
      SyndEntryI entry;
      SyndContentI description;

      List rssSnips = feeder.getFeed();
      Iterator iterator = rssSnips.iterator();
      while (iterator.hasNext()) {
        Snip rssSnip = (Snip) iterator.next();

        entry = new SyndEntry();
        entry.setTitle(rssSnip.getName());
        entry.setLink(url + "/" + rssSnip.getNameEncoded());
        // entry.setPublishedDate();
        description = new SyndContent();
        description.setType("text/html");
        description.setValue(rssSnip.getXMLContent());
        entry.setDescription(description);
        entry.setAuthor(rssSnip.getCUser());
        entry.setPublishedDate(rssSnip.getModified().getmTime());
        entries.add(entry);

//         <item>
//        <title><c:out value="${child.name}" escapeXml="true"/></title>
//        <link><c:out value="${url}/${child.nameEncoded}"/></link>
//        <description><s:content snip="${child}" removeHtml="true" encode="true"/></description>
//        <guid isPermaLink="true"><c:out value="${url}/${child.nameEncoded}"/></guid>
//        <content:encoded><s:content snip="${child}" encode="true"/></content:encoded>
//        <s:dublinCore snip="${child}" format="xml"/>
//        <comments><c:out value="${child.comments.postUrl}"/></comments>
//      </item>
      }
      feed.setEntries(entries);

      SyndFeedOutput output = new SyndFeedOutput();
      try {
        output.output(feed, new OutputStreamWriter(response.getOutputStream()));
      } catch (FeedException e) {
        e.printStackTrace();
      }
    }
  }
}
