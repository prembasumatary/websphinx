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

import websphinx.Access;
import java.applet.Applet;
import java.applet.AppletContext;

public abstract class Context {

    static Applet applet;
    static String target;
    static AppletContext context;
    static Browser browser;
    static ScriptInterpreter interpreter;

    public static boolean isApplet () {
        return applet != null;
    }

    public static boolean isApplication () {
        return applet == null;
    }

    public static void setApplet (Applet _applet) {
        applet = _applet;
        internalSetApplet ();
    }

    public static void setApplet (Applet _applet, String _target) {
        applet = _applet;
        target = _target;
        internalSetApplet ();
    }

    private static void internalSetApplet () {
        context = applet.getAppletContext ();

        String browserName;
        try {
            browserName = System.getProperty ("browser");
        } catch (Throwable t) {
            browserName = null;
        }

        if (browserName == null) {
            // appletviewer
            browser = null;
            interpreter = null;
        }
        else if (browserName.startsWith ("Netscape")) {
            // Netscape
            Netscape ns = target != null ? new Netscape (context, target) : new Netscape(context, target);
            browser = ns;
            interpreter = ns.getScriptInterpreter ();

            String browserVersion;
            try {
                browserVersion = System.getProperty ("browser.version");
            } catch (Throwable e) {
                browserVersion = null;
            }
            if (browserVersion == null)
                browserVersion = "";
        
            if (browserVersion.startsWith ("4."))
                try {
                    Access.setAccess (new Netscape4Access ());
                } catch (Throwable t2) {
                    t2.printStackTrace ();
                } 
        }
        // NIY: Internet Explorer
        else {
            // generic browser
            browser = target != null ? new Browser (context, target) : new Browser (context);
            interpreter = null;
        }
    }

    public static Applet getApplet () {
        return applet;
    }

    public static AppletContext getAppletContext () {
        return context;
    }

    public static Browser getBrowser () {
        return browser;
    }

    public static ScriptInterpreter getScriptInterpreter () {
        return interpreter;
    }
}
