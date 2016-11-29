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
import java.io.IOException;
import rcm.awt.PopupDialog;

public class Script implements Action,LinkPredicate,PagePredicate {
    String script;
    boolean asLinkPredicate;

    transient Crawler crawler;
    transient ScriptInterpreter interp;
    transient Object function;

    public Script (String script, boolean asLinkPredicate) {
        this.script = script;
        this.asLinkPredicate = asLinkPredicate;
    }

    public String getScript () {
        return script;
    }

    public boolean equals (Object object) {
        if (! (object instanceof Script))
            return false;
        Script s = (Script)object;
        return s.script.equals (script) 
            && s.asLinkPredicate == asLinkPredicate;
    }    

    static String[] argsLink = {"crawler", "link"};
    static String[] argsPage = {"crawler", "page"};

    public void connected (Crawler crawler) {
        this.crawler = crawler;
        interp = Context.getScriptInterpreter ();
        if (interp != null) {
            try {
                 function = interp.lambda (asLinkPredicate
                                        ? argsLink : argsPage,
                                       script);
            } catch (ScriptException e) {
                PopupDialog.warn (null, "Script Error", e.toString());
                function = null;
            }
        }
    }

    public void disconnected (Crawler crawler) {
         crawler = null;
         interp = null;
         function = null;
    }

    public boolean shouldVisit (Link link) {
        try {
            if (interp == null || function == null)
                // FIX: use GUI to signal error
                throw new ScriptException ("Scripting language is not available");

            Object[] args = new Object[2];
            args[0] = crawler;
            args[1] = link;        
            return toBool (interp.apply (function, args));
        } catch (ScriptException e) {
            System.err.println (e); // FIX: use GUI when available
            return false;
        }
    }

    public boolean shouldActOn (Page page) {
        try {
            if (interp == null || function == null)
                throw new ScriptException ("Scripting language is not available");            

            Object[] args = new Object[2];
            args[0] = crawler;
            args[1] = page;
            return toBool (interp.apply (function, args));
        } catch (ScriptException e) {
            System.err.println (e); // FIX: use GUI when available
            return false;
        }
    }

    public void visit (Page page) {
        try {
            if (interp == null || function == null)
                // FIX: use GUI to signal error
              throw new ScriptException ("Scripting language is not available");

            Object[] args = new Object[2];
            args[0] = crawler;
            args[1] = page;
            interp.apply (function, args);
        } catch (ScriptException e) {
            throw new RuntimeException (e.toString());
        }
    }

    boolean toBool (Object obj) {
        if (! (obj instanceof Boolean))
            return false;
        return ((Boolean)obj).booleanValue();
    }

}
