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

public class DualPredicate implements LinkPredicate, PagePredicate {
    Object positive, negative;

    public DualPredicate (Object positive, Object negative) {
        this.positive = positive;
        this.negative = negative;
    }
    public boolean equals (Object object) {
        if (! (object instanceof DualPredicate))
            return false;
        DualPredicate p = (DualPredicate)object;
        return p.positive.equals (positive) && p.negative.equals (negative);
    }    

    public Object getPositivePredicate () {
        return positive;
    }

    public Object getNegativePredicate () {
        return negative;
    }

    public void connected (Crawler crawler) {
        if (positive instanceof LinkPredicate)
            ((LinkPredicate)positive).connected (crawler);
        else if (positive instanceof PagePredicate)
            ((LinkPredicate)positive).connected (crawler);

        if (negative instanceof LinkPredicate)
            ((LinkPredicate)negative).connected (crawler);
        else if (negative instanceof PagePredicate)
            ((LinkPredicate)negative).connected (crawler);
    }

    public void disconnected (Crawler crawler) {
        if (positive instanceof LinkPredicate)
            ((LinkPredicate)positive).disconnected (crawler);
        else if (positive instanceof PagePredicate)
            ((LinkPredicate)positive).disconnected (crawler);

        if (negative instanceof LinkPredicate)
            ((LinkPredicate)negative).disconnected (crawler);
        else if (negative instanceof PagePredicate)
            ((LinkPredicate)negative).disconnected (crawler);
    }
    
    public boolean shouldVisit (Link link) {
        return ((LinkPredicate)positive).shouldVisit (link) 
            && !((LinkPredicate)negative).shouldVisit (link);
    }
    public boolean shouldActOn (Page page) {
        return ((PagePredicate)positive).shouldActOn (page)
            && !((PagePredicate)negative).shouldActOn (page);
    }
}
