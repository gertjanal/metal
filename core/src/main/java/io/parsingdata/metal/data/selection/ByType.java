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

package io.parsingdata.metal.data.selection;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;

public final class ByType {

    private ByType() {}

    public static ParseItemList getReferences(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        return getReferences(graph, graph);
    }

    private static ParseItemList getReferences(final ParseGraph graph, final ParseGraph root) {
        if (graph.isEmpty()) { return ParseItemList.EMPTY; }
        final ParseItem head = graph.head;
        if (head.isReference() && head.asReference().resolve(root) == null) { throw new IllegalStateException("A ref must point to an existing graph."); }
        return getReferences(graph.tail, root).add(head.isGraph() ? getReferences(head.asGraph(), root) : (head.isReference() ? ParseItemList.EMPTY.add(head.asReference().resolve(root)) : ParseItemList.EMPTY));
    }

}
