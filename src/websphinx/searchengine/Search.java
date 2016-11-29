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
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class Search extends Crawler implements Enumeration {

    int maxResults;
    int walkedResults; // approximate number of results walked to

    Vector results = new Vector();  // vector of SearchEngineResults
    int nextResult = 0; // next result to be returned by the enumeration
    int approxCount = -1; // (approximate) total number of results
    boolean crawling = false;

    public Search () {
        this (Integer.MAX_VALUE);
    }

    public Search (int maxResults) {
        this.maxResults = maxResults;
        setDepthFirst (false);
        setMaxDepth (Integer.MAX_VALUE);
        EventLog.monitor (this); // FIX: debugging only
    }

    public Search (SearchEngine engine, String keywords, int maxResults) {
        this (maxResults);
        addQuery (engine, keywords);
        search ();
    }

    public Search (SearchEngine engine, String keywords) {
        this (engine, keywords, Integer.MAX_VALUE);
    }

    public void addQuery (SearchEngine engine, String keywords) {
        addRoot (new Link (engine.makeQuery (keywords)));
        addClassifier (engine);
        walkedResults += engine.getResultsPerPage ();
    }

    public void search () {
        crawling = true;
        Thread thread = new Thread (this, "Search");
        thread.setDaemon (true);
        thread.start ();
    }

    public int count () {
        synchronized (results) {
            // block until count is ready
            try {
                while (approxCount == -1 && crawling)
                    results.wait ();
            } catch (InterruptedException e) {}
            return approxCount;
        }
    }
    
    public boolean hasMoreElements () {
        synchronized (results) {
            try {
                while (nextResult >= results.size() && crawling)
                    results.wait ();
            } catch (InterruptedException e) {}

            return nextResult < results.size ();
        }
    }

    public Object nextElement () {
        return nextResult ();
    }

    public SearchEngineResult nextResult () {
        if (!hasMoreElements ())
            throw new NoSuchElementException ();
        synchronized (results) {
            SearchEngineResult result = (SearchEngineResult)results.elementAt (nextResult++);
            if (result.rank == 0)
               result.rank = nextResult;
            return result;
        }
    }

    public void run () {
        super.run ();
        synchronized (results) {
            if (approxCount == -1)
                approxCount = 0;
            crawling = false;
            results.notify ();
        }
    }

    public void visit (Page page) {
        synchronized (results) {
            if (approxCount == -1)
                approxCount = page.getNumericLabel ("searchengine.count", new Integer(0)).intValue();
            
            Region[] ser = page.getFields ("searchengine.results");
            for (int i=0; i<ser.length; ++i) {
                if (results.size() == maxResults) {
                    stop ();
                    return;
                }
                results.addElement (ser[i]);
            }
            results.notify ();
        }
    }
    
    public boolean shouldVisit (Link link) {
        if (walkedResults >= maxResults
            || !link.hasLabel ("searchengine.more-results"))
            return false;
        SearchEngine engine = (SearchEngine)link.getSource().getObjectLabel("searchengine.source");
        walkedResults += engine.getResultsPerPage ();
        return true;
    }
    

    public static void main (String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println ("Search <search engine classname> [-max n]  <keywords>*");
            return;
        }

        SearchEngine engine = (SearchEngine) Class.forName (args[0]).newInstance ();

        int max = Integer.MAX_VALUE;
        int firstKeyword = 1;
        if (args[1].equals ("-max")) {
            max = Integer.parseInt (args[2]);
            firstKeyword = 3;
        }

        Search ms = new Search (max);
        ms.addQuery (engine, concat (args, firstKeyword));
        ms.search ();
        while (ms.hasMoreElements ())
            System.out.println (ms.nextResult ());
    }

    static String concat (String[] args, int start) {
        StringBuffer buf = new StringBuffer ();
        for (int i=start; i<args.length; ++i) {
            if (buf.length() > 0)
                buf.append (' ');
            buf.append (args[i]);
        }
        return buf.toString ();
    }
    
}
