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

package nl.minvenj.nfi.metal.data;

import static nl.minvenj.nfi.metal.Util.checkNotNull;
import nl.minvenj.nfi.metal.token.Token;

public class ParseGraph implements ParseItem {

    public final ParseItem head;
    public final ParseGraph tail;
    public final boolean branched;
    public final Token definition;
    public final long size;

    public static final ParseGraph EMPTY = new ParseGraph();

    private ParseGraph() {
        head = null;
        tail = null;
        branched = false;
        definition = null;
        size = 0;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition, final boolean branched) {
        this.head = checkNotNull(head, "head");
        if (head.isValue() && branched) { throw new IllegalArgumentException("Argument branch cannot be true when head contains a ParseValue."); }
        this.tail = checkNotNull(tail, "tail");
        this.branched = branched;
        this.definition = definition;
        size = tail.size + 1;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition) {
        this(head, tail, definition, false);
    }

    public ParseGraph add(final ParseValue head) {
        if (branched) { return new ParseGraph(((ParseGraph)this.head).add(head), tail, definition, true); }
        return new ParseGraph(head, this, definition);
    }

    public ParseGraph add(final ParseRef ref) {
        if (branched) { return new ParseGraph(((ParseGraph)this.head).add(ref), tail, this.definition, true); }
        return new ParseGraph(ref, this, this.definition);
    }

    public ParseGraph addBranch() {
        if (branched) { return new ParseGraph(((ParseGraph)this.head).addBranch(), tail, definition, true); }
        return new ParseGraph(ParseGraph.EMPTY, this, definition, true);
    }

    public ParseGraph closeBranch() {
        if (!branched) { throw new IllegalStateException("Cannot close branch that is not open."); }
        if (((ParseGraph)head).branched) {
            return new ParseGraph(((ParseGraph)head).closeBranch(), tail, definition, true);
        }
        return new ParseGraph(head, tail, definition, false);
    }

    public ParseGraphList getRefs() {
        return getRefs(this);
    }

    private ParseGraphList getRefs(final ParseGraph root) {
        if (isEmpty()) { return ParseGraphList.EMPTY; }
        if (head.isRef() && ((ParseRef)head).resolve(root) == null) { throw new IllegalStateException("A ref must point to an existing graph."); }
        return tail.getRefs(root).add(head.isGraph() ? ((ParseGraph)head).getRefs(root) : (head.isRef() ? ParseGraphList.EMPTY.add(((ParseRef)head).resolve(root)) : ParseGraphList.EMPTY));
    }

    public ParseGraphList getGraphs() {
        return getNestedGraphs().add(this);
    }

    private ParseGraphList getNestedGraphs() {
        if (isEmpty()) { return ParseGraphList.EMPTY; }
        final ParseGraphList tailGraphs = tail.getNestedGraphs();
        if (head.isGraph()) { return tailGraphs.add((ParseGraph)head).add(((ParseGraph)head).getNestedGraphs()); }
        return tailGraphs;
    }

    public boolean containsValue() {
        if (isEmpty()) { return false; }
        return head.isValue() || tail.containsValue();
    }

    public ParseValue getLowestOffsetValue() {
        if (!containsValue()) { throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value."); }
        if (head.isValue()) { return tail.getLowestOffsetValue((ParseValue)head); }
        return tail.getLowestOffsetValue();
    }

    private ParseValue getLowestOffsetValue(final ParseValue lowest) {
        if (!containsValue()) { return lowest; }
        if (head.isValue()) { return tail.getLowestOffsetValue(lowest.getOffset() < ((ParseValue)head).getOffset() ? lowest : ((ParseValue)head)); }
        return tail.getLowestOffsetValue(lowest);
    }

    public boolean hasGraphAtRef(final long ref) {
        return findRef(getGraphs(), ref) != null;
    }

    static ParseGraph findRef(final ParseGraphList graphs, final long ref) {
        if (graphs.isEmpty()) { return null; }
        final ParseGraph res = findRef(graphs.tail, ref);
        if (res != null) { return res; }
        if (graphs.head.containsValue() && graphs.head.getLowestOffsetValue().getOffset() == ref) {
            return graphs.head;
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseGraph reverse() {
        return reverse(this, EMPTY);
    }

    /**
     * @param name Name of the value
     * @return The first value (bottom-up) with the provided name in this graph
     */
    public ParseValue get(final String name) {
        if (isEmpty()) { return null; }
        if (head.isValue() && ((ParseValue)head).matches(name)) { return (ParseValue)head; }
        if (head.isGraph()) {
            final ParseValue val = ((ParseGraph)head).get(name);
            if (val != null) { return val; }
        }
        return tail.get(name);
    }

    /**
     * @return The first value (bottom-up) in this graph
     */
    public ParseValue current() {
        if (isEmpty()) { return null; }
        if (head.isValue()) { return (ParseValue)head; }
        if (head.isGraph()) {
            final ParseValue val = ((ParseGraph)head).current();
            if (val != null) { return val; }
        }
        return tail.current(); // Ignore current if it's a reference (or an empty graph)
    }

    /**
     * @param name Name of the value
     * @return All values with the provided name in this graph
     */
    public ParseValueList getAll(final String name) {
        return getAll(name, ParseValueList.EMPTY);
    }

    private ParseValueList getAll(final String name, final ParseValueList result) {
        if (isEmpty()) { return result; }
        final ParseValueList tailResults = tail.getAll(name, result);
        if (head.isValue() && ((ParseValue)head).matches(name)) { return tailResults.add((ParseValue)head); }
        if (head.isGraph()) { return tailResults.add(((ParseGraph)head).getAll(name, result)); }
        return tailResults;
    }

    private ParseGraph reverse(final ParseGraph oldGraph, final ParseGraph newGraph) {
        if (oldGraph.isEmpty()) { return newGraph; }
        return reverse(oldGraph.tail, new ParseGraph(reverseItem(oldGraph.head), newGraph, definition));
    }

    private ParseItem reverseItem(final ParseItem item) {
        return item.isGraph() ? ((ParseGraph)item).reverse() : item;
    }

    /**
     * @param lastHead The first item (bottom-up) to be excluded
     * @return The subgraph of this graph starting past (bottom-up) the provided lastHead
     */
    public ParseGraph getGraphAfter(final ParseItem lastHead) {
        return getGraphAfter(lastHead, EMPTY);
    }

    private ParseGraph getGraphAfter(final ParseItem lastHead, final ParseGraph result) {
        if (isEmpty()) { return EMPTY; }
        if (head == lastHead) { return result; }
        return new ParseGraph(head, tail.getGraphAfter(lastHead, result), definition);
    }

    @Override public boolean isValue() { return false; }
    @Override public boolean isGraph() { return true; }
    @Override public boolean isRef() { return false; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ParseGraph(" + (head != null ? head.toString() : "null") + ", " + (tail != null ? tail.toString() : "null") + ", " + branched + ")";
    }

}
