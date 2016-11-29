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

package websphinx.workbench;

import websphinx.*;
import java.io.File;
import java.io.IOException;

public class ExtractAction implements Action, CrawlListener {
    Pattern pattern;
    String filename;
    boolean useBrowser;
    boolean textOnly;
    
    transient File file;
    transient RecordTransformer records;
    transient boolean noFields;
    
    public ExtractAction (Pattern pattern, boolean useBrowser, String filename, boolean textOnly) {
        this.pattern = pattern;
        this.filename = filename;
        this.useBrowser = useBrowser;
        this.textOnly = textOnly;
    }
    
    public boolean equals (Object object) {
        if (! (object instanceof ExtractAction))
            return false;
        ExtractAction a = (ExtractAction)object;
        return same (a.filename, filename)
            && a.useBrowser == useBrowser
            && a.pattern.equals (pattern)
            && a.textOnly == textOnly;
    }

    private boolean same (String s1, String s2) {
        if (s1 == null || s2 == null)
            return s1 == s2;
        else
            return s1.equals (s2);
    }

    public Pattern getPattern () {
        return pattern;
    }
    public boolean getUseBrowser () {
        return useBrowser;
    }
    public String getFilename () {
        return filename;
    }
    public boolean getTextOnly () {
        return textOnly;
    }
    
    public void connected (Crawler crawler) {
        crawler.addCrawlListener (this);
    }

    public void disconnected (Crawler crawler) {
        crawler.removeCrawlListener (this);
    }

    private void showit () {
      Browser browser = Context.getBrowser();
      if (browser != null)
        browser.show (file);
    }

    public synchronized void visit (Page page) {
        try {
            int n = 0;

            PatternMatcher m = pattern.match (page);
            for (Region r = m.nextMatch(); r != null; r = m.nextMatch()) {
                Object[] fields;
                if (noFields) {
                    fields = new Object[1];
                    fields[0] = r;
                }
                else
                    fields = (Object[])r.getFields (Pattern.groups);
                    
                records.writeRecord (fields, textOnly);
                ++n;
            }
            
            if (n > 0)
              records.flush ();
        } catch (IOException e) {
            throw new RuntimeException (e.toString());
        }
    }

    /**
     * Notify that the crawler started.
     */
    public synchronized void started (CrawlEvent event){
        if (records == null) {
            try {
                file = (filename != null)
                  ? new File (filename)
                  : Access.getAccess ().makeTemporaryFile ("extract", ".html");
                
                records = new RecordTransformer (file.toString());
                
                String[] fieldNames = pattern.getFieldNames ();
                noFields = (fieldNames.length == 0);
                records.setProlog (records.getProlog ()
                                   + makeTableHeader (fieldNames));
            } catch (IOException e) {
                System.err.println (e); // FIX: use GUI when available
            }        
        }
    }

    private String makeTableHeader (String[] fieldNames) {
        String result = "<TR>\n<TH>\n";
        if (fieldNames.length == 0)
            result += "<TH>\n";
        else
            for (int i=0; i<fieldNames.length; ++i)
                result += "<TH>" + fieldNames[i] + "\n";
        return result;
    }
    
    /**
     * Notify that the crawler ran out of links to crawl
     */
    public synchronized void stopped (CrawlEvent event){
        try {
            if (records != null) {
                records.close ();
                records = null;
                if (useBrowser)
                  showit ();
            }
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }

    /**
     * Notify that the crawler's state was cleared.
     */
    public synchronized void cleared (CrawlEvent event){
        try {
            if (records != null) {
                records.close ();
                records = null;
                if (useBrowser)
                  showit ();
            }
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }

    /**
     * Notify that the crawler timed out.
     */
    public synchronized void timedOut (CrawlEvent event){
        try {
            records.close ();
            records = null;
            if (useBrowser)
              showit ();
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }

    /**
     * Notify that the crawler is paused.
     */
    public synchronized void paused (CrawlEvent event){
        try {
            records.flush ();
            if (useBrowser)
              showit ();
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }
}
