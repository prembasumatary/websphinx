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
import rcm.awt.PopupDialog;

public class DownloadParametersEditor extends Panel {

    DownloadParameters dp;

    TextField maxThreads;
    TextField maxPageSize;
    TextField downloadTimeout;
    TextField crawlTimeout;
    Checkbox obeyRobotExclusion;
    TextField maxRequestsPerServer;
    TextField delay;
    Checkbox interactive;
    Checkbox useCaches;

    public DownloadParametersEditor () {
        setLayout (new GridBagLayout ());

        Constrain.add (this, new Label ("Threads:"), Constrain.labelLike (0, 0));
        Constrain.add (this, maxThreads = new TextField (),
                       Constrain.fieldLike (1,0));
        Constrain.add (this, new Label ("Page size:"), Constrain.labelLike (0,1));
        Constrain.add (this, maxPageSize = new TextField (),
                       Constrain.fieldLike (1,1));
        Constrain.add (this, new Label ("KB"), Constrain.labelLike (2,1));

        Constrain.add (this, new Label ("Page timeout:"), Constrain.labelLike (0,2));
        Constrain.add (this, downloadTimeout = new TextField (),
                       Constrain.fieldLike (1,2));
        Constrain.add (this, new Label ("sec"), Constrain.labelLike (2,2));
        Constrain.add (this, new Label ("Crawl timeout:"), Constrain.labelLike (0,3));
        Constrain.add (this, crawlTimeout = new TextField (),
                       Constrain.fieldLike (1,3));
        Constrain.add (this, new Label ("sec"), Constrain.labelLike (2,3));

//         Constrain.add (this, new Label ("Simultaneous requests:"), Constrain.labelLike (3,0));
//         Constrain.add (this, maxRequestsPerServer = new TextField (),
//                        Constrain.fieldLike (4,0));
//         maxRequestsPerServer.disable ();
//         Constrain.add (this, new Label ("Delay between requests:"), Constrain.labelLike (3,1));
//         Constrain.add (this, delay = new TextField (),
//                        Constrain.fieldLike (4,1));
//         delay.disable ();
//         Constrain.add (this, new Label ("msec"), Constrain.labelLike (5,1));

        Constrain.add (this, obeyRobotExclusion = new Checkbox ("Obey robot exclusion"),
                       Constrain.labelLike (3,0));

        Constrain.add (this, interactive = new Checkbox ("Ask user for passwords"),
                       Constrain.labelLike (3,2));
        Constrain.add (this, useCaches = new Checkbox ("Use browser cache"),
                       Constrain.labelLike (3,3));

        // grab defaults
        setDownloadParameters (new DownloadParameters());
    }

    public void setDownloadParameters (DownloadParameters dp) {
        this.dp = dp;

        maxThreads.setText (String.valueOf (dp.getMaxThreads ()));
        maxPageSize.setText (String.valueOf (dp.getMaxPageSize ()));
        downloadTimeout.setText (String.valueOf (dp.getDownloadTimeout ()));
        crawlTimeout.setText (String.valueOf (dp.getCrawlTimeout ()));
        obeyRobotExclusion.setState (dp.getObeyRobotExclusion ());
        //maxRequestsPerServer.setText (String.valueOf (dp.getMaxRequestsPerServer ()));
        //delay.setText (String.valueOf (dp.getDelay ()));
        interactive.setState (dp.getInteractive ());
        useCaches.setState (dp.getUseCaches ());
    }

    public DownloadParameters getDownloadParameters () {
        dp = dp
            .changeMaxThreads (Integer.parseInt (maxThreads.getText()))
            .changeMaxPageSize (Integer.parseInt (maxPageSize.getText()))
            .changeDownloadTimeout (Integer.parseInt (downloadTimeout.getText()))
            .changeCrawlTimeout (Integer.parseInt (crawlTimeout.getText()))
            .changeObeyRobotExclusion (obeyRobotExclusion.getState ())
            //.changeMaxRequestsPerServer (Integer.parseInt (maxRequestsPerServer.getText()))
            //.changeDelay (Integer.parseInt (delay.getText()))
            .changeInteractive (interactive.getState ())
            .changeUseCaches (useCaches.getState ());
        return dp;
    }
}
