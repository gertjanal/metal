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

package io.parsingdata.metal.data;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class Environment {

    public final ParseGraph order;
    public final ByteStream input;
    public final long offset;
    public final long sequenceId;

    private Environment(final ParseGraph order, final ByteStream input, final long offset, final long sequenceId) {
        this.order = order;
        this.input = input;
        this.offset = offset;
        this.sequenceId = sequenceId;
    }

    public Environment(final ByteStream input, final long offset) {
        this(ParseGraph.EMPTY, input, offset, 0);
    }

    public Environment(final ByteStream input) {
        this(ParseGraph.EMPTY, input, 0L, 0);
    }

    private Environment newEnv(final ParseGraph order, final ByteStream input, final long offset) {
        return new Environment(order, input, offset, sequenceId + 1);
    }

    public Environment addBranch(final Token definition, final long offset) {
        return newEnv(order.addBranch(definition), input, offset);
    }

    public Environment addBranch(final Token definition) {
        return addBranch(definition, offset);
    }

    public Environment addRef(final Token token, final long ref) {
        return newEnv(order.add(new ParseRef(ref, token)), input, offset);
    }

    public Environment addValue(final String scope, final String name, final Token definition, final long offset, final byte[] data, final int size, final Encoding enc) {
        final long sequenceId = this.sequenceId + 1;
        final ParseValue value = new ParseValue(scope, name, definition, size, data, enc, sequenceId);
        final Environment newEnv = new Environment(order.add(value), input, offset + size, sequenceId);
        return newEnv;
    }

    @Override
    public String toString() {
        return "stream: " + input + "; offset: " + offset + "; order: " + order;
    }

    public Environment skip(final long size) {
        return newEnv(order, input, offset + size);
    }

    public Environment closeBranch(final long offset) {
        return newEnv(order.closeBranch(), input, offset);
    }

    public Environment closeBranch() {
        return closeBranch(offset);
    }
}
