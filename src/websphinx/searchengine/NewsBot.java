/*
 * WebSphinx web-crawling toolkit
 *
 * Copyright (c) 1998-2002 Carnegie Mellon University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY CARNEGIE MELLON UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package websphinx.searchengine;

import websphinx.*;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;

/**
 * <A href="http://www.newbot.com/">NewsBot</a> search engine.
 */
public class NewsBot implements SearchEngine {

    static Pattern patTitle = new Regexp ("^");

    static Pattern patCount = new Regexp (
        "Returned <B>(\\d+)</b> results"
    );
    static Pattern patNoHits = new Regexp (
        "Sorry -- your search yielded no results"
    );

    // FIX: works only for Netscape
    static Pattern patResult = new Tagexp (
            "<font>"
           +"(?{link}<A>(?{title})</A>)"
           +"</font>"
           +"<br>"
           +"<font></font>(?{description})<br>"
           +"<font><b></b></font><p>"
    );

    static Pattern patMoreLink = new Tagexp (
        "<input type=image name=act.next>"
    );

    /**
     * Classify a page.  Sets the following labels:
     * <TABLE>
     * <TR><TH>Name <TH>Type  <TH>Meaning
     * <TR><TD>searchengine.source <TD>Page label <TD>NewsBot object that labeled this page
     * <TR><TD>searchengine.count <TD>Page field <TD>Number of results on page
     * <TR><TD>searchengine.results <TD>Page fields <TD>Array of results.  Each result region
     * contains subfields: rank, title, description, and link.
     * <TR><TD>searchengine.more-results <TD>Link label <TD>Link to a page containing more results.
     * </TABLE>
     */
    public void classify (Page page) {
        String title = page.getTitle ();
        if (title != null && title.startsWith ("HotBot results:")) {
            page.setObjectLabel ("searchengine.source", this);

            Region count = patCount.oneMatch (page);
            if (count != null)
                page.setField ("searchengine.count", count.getField ("0"));
            
            Region[] results = patResult.allMatches (page);
            SearchEngineResult[] ser = new SearchEngineResult[results.length];
            for (int i=0; i<results.length; ++i) {
                ser[i] = new SearchEngineResult (results[i]);
                //System.out.println (ser[i]);
            }
            page.setFields ("searchengine.results", ser);

            PatternMatcher m = patMoreLink.match (page);
            while (m.hasMoreElements ()) {
                Link link = (Link)m.nextMatch ();
                link.setLabel ("searchengine.more-results");
                link.setLabel ("hyperlink");             
            }
        }
        else System.err.println ("not a NewsBot page");

    }

    /**
     * Priority of this classifier.
     */
    public static final float priority = 0.0F;
    
    /**
     * Get priority of this classifier.
     * @return priority.
     */
    public float getPriority () {
        return priority;
    }

    /**
     * Make a query URL for NewsBot.
     * @param keywords list of keywords, separated by spaces
     * @return URL that submits the keywords to NewsBot.
     */
    public URL makeQuery (String keywords) {
        try {
            java.util.StringTokenizer tok = new java.util.StringTokenizer (keywords);
            StringBuffer output = new StringBuffer ();
            while (tok.hasMoreElements ()) {
                String kw = tok.nextToken ();
                if (output.length () > 0)
                    output.append (" or ");
                output.append (kw);
            }

            return new URL(
"http://engine.newbot.com/newbot/server/query.fpl?client_id=0sQaJNoAahXc&output=hotbot4&logad=1&client_sw=html&client_vr=0.9&client_last_updated=ignore&T0=hotbot&S0=date&P0=&F0=24&Q0="
                           + URLEncoder.encode(output.toString())
+ "&max_results=50&S0=rank&Search.x=55&Search.y=4"
);
        } catch (MalformedURLException e) {
            throw new RuntimeException ("internal error");
        }
    }

    /**
     * Get number of results per page for this search engine.
     * @return typical number of results per page
     */
    public int getResultsPerPage () {
        return 10;
    }

    /**
     * Search NewsBot.
     * @param keywords list of keywords, separated by spaces
     * @return enumeration of SearchEngineResults returned by a NewsBot query constructed from the keywords.
     */
    public static Search search (String keywords) {
        return new Search (new NewsBot(), keywords);
    }

    /**
     * Search NewsBot.
     * @param keywords list of keywords, separated by spaces
     * @param maxResults maximum number of results to return
     * @return enumeration of SearchEngineResults returned by an NewsBot query constructed from the keywords.
     * The enumeration yields at most maxResults objects.
     */
    public static Search search (String keywords, int maxResults) {
        return new Search (new NewsBot(), keywords, maxResults);
    }
} 
