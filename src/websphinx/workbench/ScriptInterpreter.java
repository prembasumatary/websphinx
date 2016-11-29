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

public interface ScriptInterpreter {
    /**
     * Return name of language this interpreter handles.
     * @return Language name, such as "Javascript" or "TCL"
     */
    public abstract String getLanguage ();

    /**
     * Evaluate an expression in the script language.
     * @param expression Expression to evaluate
     * @exception ScriptException if execution encounters an error
     */
    public abstract Object eval (String expression) throws ScriptException;

    /**
     * Construct a procedure or function.
     * @param args Argument names
     * @param body Function body
     * @return Function object suitable for apply()
     * @exception ScriptException if execution encounters an error
     */
    public abstract Object lambda (String[] args, String body) throws ScriptException;

    /**
     * Call a procedure or function.
     * @param func Function object (previously returned by lambda()
     * @param args Arguments for the function
     * @exception ScriptException if execution encounters an error
     */
    public abstract Object apply (Object func, Object[] args) throws ScriptException;

    /**
     * Set a variable in the interpreter's global namespace
     * @param name Name of variable
     * @param object New value for variable
     */
    public abstract void set (String name, Object object);

    /**
     * Get a variable defined in the interpreter's global
     * namespace
     * @param name Name of variable to get
     * @return Value of variable, or null if not defined
     */
    public abstract Object get (String name);
}
