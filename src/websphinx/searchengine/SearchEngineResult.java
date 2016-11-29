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

/**
 * Result returned by a search engine query, identifying a Web page that matches the query.
 */
public class SearchEngineResult extends Region {
    /**
     * Relevancy rank of page in search engine's ordering.  In other words, rank=1 
     * is the first result the search engine returned.  If search engine
     * results are not explicitly numbered, then rank may be 0.
     */
    public int rank = 0;

    /**
     * Relevancy score of page, by search engine's scale.  If search engine
     * does not provide a score, the score defaults to 0.0. 
     * 
     */
    public double score = 0.0;

    /**
     * Title of page as reported by search engine, or null if not provided
     */
    public String title;

    /**
     * Short description of page as reported by search engine.  Typically the first few words
     * of the page.  If not provided, description is null.
     */
    public String description;

    /**
     * Link to the actual page.
     */
    public Link link;

    /**
     * Search engine that produced this hit.
     */
    public SearchEngine searchengine;

    /**
     * Make a SearchEngineResult.
     * @param result Region of a search engine's results page.  Should be annotated with rank, title,
     * description, and link fields.
     */
    public SearchEngineResult (Region result) {
        super (result);
        rank = result.getNumericLabel ("rank", new Integer(0)).intValue();
        score = result.getNumericLabel ("score", new Double(0)).doubleValue();
        title = result.getLabel ("title");
        description = result.getLabel ("description");
        
        try {
            link = (Link)result.getField ("link");
        } catch (ClassCastException e) {}
        searchengine = (SearchEngine)result.getSource().getObjectLabel ("searchengine.source");
    }

    public String toString () {
        return rank + ". " + title + " [" + (link!=null ? link.getURL ().toString() : "(null)") + "]" + " " + score + "\n"
               + "    " + description;
    }
} 
