/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.tools.jshexviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Sub;

/**
 * Generate a HTML page to view the Metal ParseGraph in a hex viewer.
 */
public class JsHexViewer {

    private static final int COLUMN_COUNT = 1 << 5;
    private static List<Long> REFS;

    public static void generate(final ParseGraph graph) throws IOException {
        final Map<Long, LinkedList<Definition>> map = new TreeMap<>();
        step(graph, graph, map);

        try {
            final File root = new File(JsHexViewer.class.getResource("/").toURI());

            final File file = new File(root, "jsHexViewer.htm");
            try (FileWriter out = new FileWriter(file);
                 InputStream in = JsHexViewer.class.getResourceAsStream("/jsHexViewer/jsHexViewer.htm");
                 BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<!-- generated -->")) {
                        if (line.trim().startsWith("var locations =")) {
                            out.write("    var locations = ");
                            out.write(map.keySet().toString());
                            out.write(";");
                        }
                        else if (line.trim().startsWith("var data =")) {
                            writeData(out, map);
                        }
                        else if (line.trim().startsWith("var columnCount =")) {
                            out.write("var columnCount = " + COLUMN_COUNT + ";");
                        }
                    }
                    else {
                        out.write(line);
                    }
                    out.write('\n');
                }
            }
        }
        catch (final URISyntaxException e) {
            throw new IOException(e);
        }
    }

    private static void writeData(final FileWriter out, final Map<Long, LinkedList<Definition>> map) throws IOException {
        out.write("    var data = [");
        for (final Long row : map.keySet()) {
            out.write(map.get(row).toString());
            out.write(",");
        }
        out.write("[]];");
    }

    private static void step(final ParseItem item, final ParseGraph root, final Map<Long, LinkedList<Definition>> map) {
        if (!item.isGraph()) {
            if (item.getDefinition() instanceof Def) {
                final ParseValue value = item.asValue();
                if (REFS != null) {
                    getList(map, new Definition(value, REFS));
                    REFS = null;
                }
                else {
                    getList(map, new Definition(value));
                }
            }
            return;
        }
        else if (item.getDefinition() instanceof Sub) {
            REFS = resolveSub(item, root);
        }

        if (item.asGraph().head == null) {
            return;
        }
        step(item.asGraph().tail, root, map);
        step(item.asGraph().head, root, map);
    }

    private static List<Long> resolveSub(final ParseItem item, final ParseGraph root) {
        final Sub sub = (Sub)item.getDefinition();
        final ValueExpression exp = sub.addr;

        final List<Long> refOffsets = new ArrayList<>();
        resolveExpression(item, exp, root, refOffsets);
        return refOffsets;
    }

    private static void resolveExpression(final ParseItem item, final ValueExpression exp, final ParseGraph root, final List<Long> refOffsets) {
        if (exp instanceof Ref) {
            final long offset = resolveRefOffset(item, ((Ref) exp).name, root);
            if (offset != 0) {
                refOffsets.add(offset);
            }
        }
        else if (exp instanceof BinaryValueExpression) {
            resolveExpression(item, ((BinaryValueExpression) exp).lop, root, refOffsets);
            resolveExpression(item, ((BinaryValueExpression) exp).rop, root, refOffsets);
        }
    }

    private static long resolveRefOffset(final ParseItem item, final String name, final ParseGraph root) {
        ParseValueList list = root.getAll(name);
        ParseValue best = null;
        while (list.head != null) {
            //if (list.head.sequenceId <= item.getSequenceId()) {
                if (best == null || list.head.sequenceId > best.sequenceId) {
                    best = list.head;
                }
            //}
            list = list.tail;
        }
        if (best != null) {
            return best.offset;
        }
        else {
            return 0;
        }
    }

    private static void getList(final Map<Long, LinkedList<Definition>> map, final Definition definition) {
        final long row = definition._offset / COLUMN_COUNT;
        LinkedList<Definition> list = map.get(row);
        if (list == null) {
            list = new LinkedList<>();
            map.put(row, list);
        }
        list.addFirst(definition);
    }

    private static class Definition {
        private final String _name;
        private final long _offset;
        private final long _size;
        private final List<Long> _refs;

        public Definition(final ParseValue value, final List<Long> refs) {
            _name = value.getFullName().substring(2); // W.
            _offset = value.getOffset();
            _size = value.getValue().length;
            _refs = refs;
        }

        public Definition(final ParseValue value) {
            this(value, new ArrayList<Long>());
        }

        @Override
        public String toString() {
            return "[" + _offset + ", " + _size + ", '" + _name + "', " + _refs + "]";
        }
    }
}
