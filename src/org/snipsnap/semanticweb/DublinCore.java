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

package org.snipsnap.semanticweb;

import org.snipsnap.snip.Snip;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;

import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;

/**
 * Generates a map with dublin core entries
 * see http://www.ietf.org/rfc/rfc2731.txt and
 * http://dublincore.org/documents/dces/
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DublinCore {
  private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
  private static SimpleDateFormat year = new SimpleDateFormat("yyyy");
  private static AppConfiguration conf = Application.get().getConfiguration();

  public static Map generate(Snip snip) {
    Map dublinCore = new HashMap();
    dublinCore.put("Creator", snip.getCUser());
    dublinCore.put("Title", snip.getName());
    dublinCore.put("Date", sf.format(snip.getModified().getmTime()));
    dublinCore.put("Type", "Text");
    dublinCore.put("Identifier", Application.get().getConfiguration().getSnipUrl(snip.getName()));
    dublinCore.put("Coyright", "Copyright " + year.format(snip.getModified().getmTime()));
    dublinCore.put("Language", conf.getLocale());
    return dublinCore;
  }

  /**
   * Identifier:  Title
   * Definition:  A name given to the resource.
   * Comment:     Typically, a Title will be a name by which the resource is
   *              formally known.
   *
   * Identifier:  Creator
   * Definition:  An entity primarily responsible for making the content of
   *              the resource.
   * Comment:     Examples of a Creator include a person, an organisation,
   *              or a service.
   *              Typically, the name of a Creator should be used to
   *              indicate the entity.
   *
   * Identifier:  Subject
   * Definition:  The topic of the content of the resource.
   * Comment:     Typically, a Subject will be expressed as keywords,
   *              key phrases or classification codes that describe a topic
   *              of the resource.
   *              Recommended best practice is to select a value from a
   *              controlled vocabulary or formal classification scheme.
   *
   * Identifier:  Description
   * Definition:  An account of the content of the resource.
   * Comment:     Description may include but is not limited to: an abstract,
   *              table of contents, reference to a graphical representation
   *              of content or a free-text account of the content.
   *
   * Identifier:  Publisher
   * Definition:  An entity responsible for making the resource available
   * Comment:     Examples of a Publisher include a person, an organisation,
   *              or a service.
   *              Typically, the name of a Publisher should be used to
   *              indicate the entity.
   *
   * Identifier:  Contributor
   * Definition:  An entity responsible for making contributions to the
   *              content of the resource.
   * Comment:     Examples of a Contributor include a person, an organisation,
   *              or a service.
   *              Typically, the name of a Contributor should be used to
   *              indicate the entity.
   *
   * Identifier:  Date
   * Definition:  A date associated with an event in the life cycle of the
   *              resource.
   * Comment:     Typically, Date will be associated with the creation or
   *              availability of the resource.  Recommended best practice
   *              for encoding the date value is defined in a profile of
   *              ISO 8601 [W3CDTF] and follows the YYYY-MM-DD format.
   *
   * Identifier:  Type
   * Definition:  The nature or genre of the content of the resource.
   * Comment:     Type includes terms describing general categories, functions,
   *              genres, or aggregation levels for content. Recommended best
   *              practice is to select a value from a controlled vocabulary
   *              (for example, the working draft list of Dublin Core Types
   *              [c]). To describe the physical or digital manifestation
   *              of the resource, use the FORMAT element.
   * http://dublincore.org/documents/dcmi-type-vocabulary/
   *
   * Identifier:  Format
   * Definition:  The physical or digital manifestation of the resource.
   * Comment:     Typically, Format may include the media-type or dimensions of
   *              the resource. Format may be used to determine the software,
   *              hardware or other equipment needed to display or operate the
   *              resource. Examples of dimensions include size and duration.
   *              Recommended best practice is to select a value from a
   *              controlled vocabulary (for example, the list of Internet Media
   *              Types [MIME] defining computer media formats).
   *
   * Identifier:  Identifier
   * Definition:  An unambiguous reference to the resource within a given context.
   * Comment:     Recommended best practice is to identify the resource by means
   *              of a string or number conforming to a formal identification
   *              system.
   *              Example formal identification systems include the Uniform
   *              Resource Identifier (URI) (including the Uniform Resource
   *              Locator (URL)), the Digital Object Identifier (DOI) and the
   *              International Standard Book Number (ISBN).
   *
   * Identifier:  Source
   * Definition:  A Reference to a resource from which the present resource
   *              is derived.
   * Comment:     The present resource may be derived from the Source resource
   *              in whole or in part.  Recommended best practice is to reference
   *              the resource by means of a string or number conforming to a
   *              formal identification system.
   *
   * Identifier:  Language
   * Definition:  A language of the intellectual content of the resource.
   * Comment:     Recommended best practice for the values of the Language
   *              element is defined by RFC 1766 [RFC1766] which includes
   *              a two-letter Language Code (taken from the ISO 639
   *              standard [ISO639]), followed optionally, by a two-letter
   *              Country Code (taken from the ISO 3166 standard [ISO3166]).
   *              For example, 'en' for English, 'fr' for French, or
   *              'en-uk' for English used in the United Kingdom.
   *
   * Identifier:  Relation
   * Definition:  A reference to a related resource.
   * Comment:     Recommended best practice is to reference the resource by means
   *              of a string or number conforming to a formal identification
   *              system.
   *
   * Identifier:  Coverage
   * Definition:  The extent or scope of the content of the resource.
   * Comment:     Coverage will typically include spatial location (a place name
   *              or geographic coordinates), temporal period (a period label,
   *              date, or date range) or jurisdiction (such as a named
   *              administrative entity).
   *              Recommended best practice is to select a value from a
   *              controlled vocabulary (for example, the Thesaurus of Geographic
   *              Names [TGN]) and that, where appropriate, named places or time
   *              periods be used in preference to numeric identifiers such as
   *              sets of coordinates or date ranges.
   *
   * Identifier: Rights
   * Definition: Information about rights held in and over the resource.
   * Comment:    Typically, a Rights element will contain a rights
   *             management statement for the resource, or reference
   *             a service providing such information. Rights information
   *             often encompasses Intellectual Property Rights (IPR),
   *             Copyright, and various Property Rights.
   *             If the Rights element is absent, no assumptions can be made
   *             about the status of these and other rights with respect to
   *             the resource.
   *
   */
}
