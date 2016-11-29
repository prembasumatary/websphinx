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
import java.applet.AppletContext;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

public class Browser implements LinkViewListener {
    protected AppletContext context;
    protected String frameName;

    public Browser (AppletContext context) {
        this.context = context;
        frameName = null;
    }

    public Browser (AppletContext context, String frameName) {
        this.context = context;
        this.frameName = frameName;
    }

    public void show (Page page) {
        URL url = page.getURL ();

        if (url != null)
            show (url);
        else {
            // assume page was dynamically-generated
            // save it to a temporary file, and show that
            try {
                File f = Access.getAccess ().makeTemporaryFile ("sphinx", ".html");
                HTMLTransformer out = new HTMLTransformer (f.toString());
                out.writePage (page);
                out.close ();
                show (Link.FileToURL (f));
            } catch (Exception e) {
                System.err.println (e); // FIX: use GUI to report error
            }
        }
    }

    public void show (Link link) {
        show (link.getURL ());
    }

    public void show (URL url) {
        if (frameName != null)
            context.showDocument (url, frameName);
        else
            context.showDocument (url);
    }

    public void show (File file) {
      try {
        show (Link.FileToURL (file));
      } catch (MalformedURLException e) {
      }
    }

    public void viewLink (LinkViewEvent event) {
        show (event.getLink ());
    }
}
