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

import java.applet.*;
import java.awt.*;
import java.net.URL;
import websphinx.Crawler;
import websphinx.Tagexp;
import rcm.awt.PopupDialog;

public class WorkbenchApplet extends Applet {

    Workbench workbench;

    public Workbench makeWorkbench () {
        String openURL = getParameter ("open");
        String newClassname = getParameter ("new");
        
        try {
            if (openURL != null)
                return new Workbench (new URL (getDocumentBase (), openURL));
            else if (newClassname != null)
                return new Workbench ((Crawler)Class.forName (newClassname).newInstance());
            else
                return new Workbench ();        
        } catch (Exception e) {
            PopupDialog.warn (null, 
                              "Error", 
                              e.toString());
            throw new Error (e.toString());
        }
    }

    public void init () {
        super.init ();

        String targetName = getParameter ("target");
        if (targetName != null)
            Context.setApplet (this, targetName);
        else
            Context.setApplet (this);

        workbench = makeWorkbench ();

        String param;
        if ((param = getParameter ("advanced")) != null)
            workbench.setAdvancedMode (isTrue (param));

        /*
        if ((param = getParameter ("graph")) != null)
            workbench.setGraphVisible (isTrue (param));
            
        if ((param = getParameter ("statistics")) != null)
            workbench.setStatisticsVisible (isTrue (param));
            
        if ((param = getParameter ("log")) != null)
            workbench.setLoggerVisible (isTrue (param));
        */

        Crawler crawler = workbench.getCrawler();

        String action = getParameter ("action");
        if (action != null) {
            String filename = getParameter ("filename");
            String pattern = getParameter ("pattern");
            
            if (action.equalsIgnoreCase ("concatenate"))
                crawler.setAction (new ConcatAction (filename, true));
            else if (action.equalsIgnoreCase ("save"))
                crawler.setAction (new MirrorAction (filename, true));
            else if (action.equalsIgnoreCase ("visualize")) {
                crawler.setAction (null);
                //workbench.setGraphVisible (true);
            }
            else if (action.equalsIgnoreCase ("extract"))
                crawler.setAction (new ExtractAction (new Tagexp (pattern), 
                                                  true, filename, false));
            else if (action.equalsIgnoreCase ("none"))
                crawler.setAction (null);
            else
                throw new RuntimeException ("unknown action: " +action);
        }
        
        String urls = getParameter ("urls");
        if (urls != null)
            try {
                crawler.setRootHrefs (urls);
            } catch (java.net.MalformedURLException e) {
                throw new RuntimeException (e.toString());
            }
        
        String domain = getParameter ("domain");
        if (domain != null) {
            if (domain.equalsIgnoreCase ("server"))
                crawler.setDomain (Crawler.SERVER);
            else if (domain.equalsIgnoreCase ("subtree"))
                crawler.setDomain (Crawler.SUBTREE);
            else
                crawler.setDomain (Crawler.WEB);
        }
        
        String type = getParameter ("type");
        if (type != null) {
            if (type.equalsIgnoreCase ("images+hyperlinks"))
                crawler.setLinkType (Crawler.HYPERLINKS_AND_IMAGES);
            else if (type.equalsIgnoreCase ("all"))
                crawler.setLinkType (Crawler.ALL_LINKS);
            else
                crawler.setLinkType (Crawler.WEB);
        }

        String depth = getParameter ("depth");
        if (depth != null)
            crawler.setMaxDepth (Integer.parseInt (depth));

        String dfs = getParameter ("depthfirst");
        if (dfs != null)
            crawler.setDepthFirst (isTrue (dfs));

        workbench.setCrawler (crawler);

        setLayout (new BorderLayout ());
        add ("Center", workbench);
    }

    private static boolean isTrue (String s) {
        return s != null && 
            (s.equalsIgnoreCase ("on") 
             || s.equalsIgnoreCase ("1") 
             || s.equalsIgnoreCase ("yes") 
             || s.equalsIgnoreCase ("true"));
    }
}
