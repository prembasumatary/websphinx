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

public class ClassifierListEditor extends Panel {

    List classifierList;
    Button newClassifierButton;
    Button loadClassifierButton;
    Button removeClassifierButton;

    Crawler crawler;
    Classifier[] classifiers;

    public ClassifierListEditor () {
        setLayout (new GridBagLayout ());

        Constrain.add (this, new Label ("Classifiers:"), Constrain.labelLike (0, 0));
        Constrain.add (this, classifierList = new List (5, false), Constrain.areaLike (0, 1));

        Panel panel = new Panel ();
        Constrain.add (this, panel, Constrain.fieldLike (0, 2));

        panel.add (newClassifierButton = new Button ("New..."));
        panel.add (loadClassifierButton = new Button ("Load..."));
        loadClassifierButton.disable ();
        panel.add (removeClassifierButton = new Button ("Remove"));
        removeClassifierButton.disable ();
    }

    public boolean handleEvent (Event event) {
        if (event.target == classifierList) {
            if (classifierList.getSelectedIndex () != -1)
                removeClassifierButton.enable ();
            else
                removeClassifierButton.disable ();
        }
        else if (event.id == Event.ACTION_EVENT) {
            if (event.target == newClassifierButton)
                newClassifier (null);
            else if (event.target == loadClassifierButton)
                    ; // NIY
            else if (event.target == removeClassifierButton)
                removeSelectedClassifier ();
            else
                return super.handleEvent (event);
        }
        else
            return super.handleEvent (event);

        return true;
    }

    public void setCrawler (Crawler crawler) {
        this.crawler = crawler;
        scan ();
    }

    public Crawler getCrawler () {
        return crawler;
    }

    private void newClassifier (String className) {
        if (className == null || className.length() == 0) {
            className = PopupDialog.ask (this,
                                         "New Classifier",
                                         "Create an instance of class:");
            if (className == null)
                return;
        }
        
        try {
            Class classifierClass = (Class)Class.forName (className);
            Classifier cl = (Classifier)classifierClass.newInstance ();
            crawler.addClassifier (cl);
        } catch (Exception e) {
            PopupDialog.warn (this, 
                              "Error", 
                              e.toString());
        }
        
        scan ();
    }

    private void removeSelectedClassifier () {
        int i = classifierList.getSelectedIndex ();
        if (i < 0 || i >= classifiers.length) {
            removeClassifierButton.disable ();
            return;
        }

        crawler.removeClassifier (classifiers[i]);
        scan ();        
    }

    private void scan () {
        classifiers = crawler.getClassifiers ();
        classifierList.clear ();
        for (int i=0; i<classifiers.length; ++i)
            classifierList.addItem (classifiers[i].getClass().getName());
    }
}
