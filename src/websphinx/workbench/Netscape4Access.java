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
import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import netscape.security.PrivilegeManager;
import netscape.security.ForbiddenTargetException;

public class Netscape4Access extends Access {

    private boolean isLocalURL (URL url) {
        return (url.getProtocol().equals ("file")
                     && url.getHost().equals (""));
    }

    public URLConnection openConnection (URL url) throws IOException {
        try {                             
            PrivilegeManager.enablePrivilege ("UniversalConnectWithRedirect");
            if (isLocalURL (url))
                PrivilegeManager.enablePrivilege ("UniversalFileRead");
        } catch (ForbiddenTargetException e) {
          throw new IOException ("connection forbidden");
        }

        return super.openConnection (url);
    }

    public URLConnection openConnection (Link link) throws IOException {
        try {
            PrivilegeManager.enablePrivilege ("UniversalConnectWithRedirect");
        } catch (ForbiddenTargetException e) {
          throw new IOException ("connection forbidden");
        }

        if (isLocalURL (link.getURL()))
          PrivilegeManager.enablePrivilege ("UniversalFileRead");
        return super.openConnection (link);
    }

  public InputStream readFile (File file) throws IOException {
    try {
      PrivilegeManager.enablePrivilege("UniversalFileRead");
    } catch (ForbiddenTargetException e) {
      throw new IOException ("file read forbidden");
    }

    return super.readFile (file);
  }

  public OutputStream writeFile (File file, boolean append) throws IOException {
    try {
      PrivilegeManager.enablePrivilege("UniversalFileWrite");
    } catch (ForbiddenTargetException e) {
      throw new IOException ("file write forbidden");
    }

    return super.writeFile (file, append);
  }

  public RandomAccessFile readWriteFile (File file) throws IOException {
    try {
      PrivilegeManager.enablePrivilege("UniversalFileWrite");
      PrivilegeManager.enablePrivilege("UniversalFileRead");
    } catch (ForbiddenTargetException e) {
      throw new IOException ("file read/write forbidden");
    }
    
    return super.readWriteFile (file);
  }

    public void makeDir (File file) throws IOException {
        try {
          PrivilegeManager.enablePrivilege("UniversalFileWrite");
          PrivilegeManager.enablePrivilege("UniversalFileRead");
            // mkdirs needs UniversalFileRead to check whether a 
            // directory already exists (I guess)
        } catch (ForbiddenTargetException e) {
          throw new IOException ("make-directory forbidden");
        }
        
        super.makeDir (file);
    }

    
  public File makeTemporaryFile (String basename, String extension) {
    try {
      PrivilegeManager.enablePrivilege("UniversalFileRead"); 
            // need UniversalFileRead to check whether a filename
            // already exists
            // FIX: should I bother with that check?
    } catch (ForbiddenTargetException e) {
      throw new SecurityException ("temp file check forbidden");
    }
      
    return super.makeTemporaryFile (basename, extension);
  }
}
