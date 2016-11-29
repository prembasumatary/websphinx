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
import java.awt.*;
import rcm.awt.Constrain;

public class SimpleCrawlerEditor extends CrawlerEditor {

    Label actionLabel;
    ActionEditor actionEditor;

    public SimpleCrawlerEditor () {
        super ();

        // remove all the pieces we don't need
        remove (typeLabel);
        remove (typeChoice);
        remove (depthLabel);
        remove (depthField);
        remove (depthLabel2);
        remove (searchOrderChoice);

        // add an action editor
        actionLabel = new Label("Action:");
        actionEditor = new ActionEditor ();
        Constrain.add (this, actionLabel, 
                       Constrain.labelLike (0, 4));
        Constrain.add (this, actionEditor, 
                       Constrain.areaLike (1, 4, 4));
    }

    public void setCrawler (Crawler crawler) {
        super.setCrawler (crawler);
        actionEditor.setAction (crawler.getAction());
    }

    public Crawler getCrawler () {
        crawler.setAction (actionEditor.getAction ());
        return super.getCrawler ();
    }

}
