/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.tools.jdeps;

import com.sun.tools.classfile.Dependency.Location;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the source of the class files.
 */
public class Archive {
    private final File file;
    private final String filename;
    private final ClassFileReader reader;
    private final Map<Location, Set<Location>> deps
        = new HashMap<Location, Set<Location>>();

    public Archive(String name) {
        this.file = null;
        this.filename = name;
        this.reader = null;
    }

    public Archive(File f, ClassFileReader reader) {
        this.file = f;
        this.filename = f.getName();
        this.reader = reader;
    }

    public ClassFileReader reader() {
        return reader;
    }

    public String getFileName() {
        return filename;
    }

    public void addClass(Location origin) {
        Set<Location> set = deps.get(origin);
        if (set == null) {
            set = new HashSet<Location>();
            deps.put(origin, set);
        }
    }
    public void addClass(Location origin, Location target) {
        Set<Location> set = deps.get(origin);
        if (set == null) {
            set = new HashSet<Location>();
            deps.put(origin, set);
        }
        set.add(target);
    }

    public void visit(Visitor v) {
        for (Map.Entry<Location,Set<Location>> e: deps.entrySet()) {
            v.visit(e.getKey());
            for (Location target : e.getValue()) {
                v.visit(e.getKey(), target);
            }
        }
    }

    public String toString() {
        return file != null ? file.getPath() : filename;
    }

    interface Visitor {
        void visit(Location loc);
        void visit(Location origin, Location target);
    }
}