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
import java.net.URL;
import java.net.MalformedURLException;

public class MirrorAction implements Action, CrawlListener {
    String directory;
    boolean useBrowser;

    transient File dir;
    transient Mirror mirror;

    public MirrorAction (String directory, boolean useBrowser) {
        this.directory = directory;
        this.useBrowser = useBrowser;
    }

    public boolean equals (Object object) {
        if (! (object instanceof MirrorAction))
            return false;
        MirrorAction a = (MirrorAction)object;
        return same (a.directory, directory)
            && a.useBrowser == useBrowser;
    }    

    private boolean same (String s1, String s2) {
        if (s1 == null || s2 == null)
            return s1 == s2;
        else
            return s1.equals (s2);
    }

    public String getDirectory () {
        return directory;
    }

    public boolean getUseBrowser () {
        return useBrowser;
    }

    private void showit () {
        Browser browser = Context.getBrowser();
        if (browser != null)
            try {
                browser.show (Link.FileToURL (dir));
            } catch (MalformedURLException e) {}
    }

    public synchronized void visit (Page page) {
        try {
            mirror.writePage (page);
        } catch (IOException e) {
            throw new RuntimeException (e.toString());
        }
    }

    public void connected (Crawler crawler) {
        crawler.addCrawlListener (this);
    }

    public void disconnected (Crawler crawler) {
        crawler.removeCrawlListener (this);
    }

    /**
     * Notify that the crawler started.
     */
    public void started (CrawlEvent event){
        if (mirror == null) {
            try {
                dir = (directory != null)
                  ? new File (directory)
                  : Access.getAccess ().makeTemporaryFile ("mirror", "");
                mirror = new Mirror (dir.toString());
                
                Crawler crawler = event.getCrawler ();
                Link[] roots = crawler.getRoots ();
                for (int i=0; i<roots.length; ++i)
                    mirror.mapDir (roots[i].getURL(), dir.toString());
            } catch (IOException e) {
                System.err.println (e); // FIX: use GUI when available
            }        
        }
    }

    /**
     * Notify that the crawler ran out of links to crawl
     */
    public void stopped (CrawlEvent event){
        try {
            if (mirror != null) {
                mirror.close ();
                mirror = null;
                
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
    public void cleared (CrawlEvent event){
        try {
            if (mirror != null) {
                mirror.close ();
                mirror = null;
                
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
    public void timedOut (CrawlEvent event){
        try {
            if (mirror != null) {
                mirror.close ();
                mirror = null;
                
                if (useBrowser)
                    showit ();
            }
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }

    /**
     * Notify that the crawler is paused.
     */
    public void paused (CrawlEvent event){
        try {
            if (mirror != null) {
                mirror.rewrite ();
                if (useBrowser)
                    showit ();
            }
        } catch (IOException e) {
            System.err.println (e); // FIX: use GUI when available
        }
    }

}

