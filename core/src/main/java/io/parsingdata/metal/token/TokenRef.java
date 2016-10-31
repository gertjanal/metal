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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ParseResult.failure;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

public class TokenRef extends Token {

    private static final Token LOOKUP_FAILED = new Token("LOOKUP_FAILED", null) {
        @Override
        protected ParseResult parseImpl(String scope, Environment environment, Encoding encoding) throws IOException {
            return failure(environment);
        }
    };

    public final String referenceName;

    public TokenRef(String name, String referenceName, Encoding encoding) {
        super(name, encoding);
        this.referenceName = checkNotNull(referenceName, "referenceName");
        if (referenceName.isEmpty()) { throw new IllegalArgumentException("Argument referenceName may not be empty."); }
    }

    @Override
    protected ParseResult parseImpl(String scope, Environment environment, Encoding encoding) throws IOException {
        return lookup(environment.order, referenceName).parse(scope, environment, encoding);
    }

    private Token lookup(final ParseItem item, final String referenceName) {
        if (item.getDefinition().name.equals(referenceName)) { return item.getDefinition(); }
        if (!item.isGraph() || item.asGraph().isEmpty()) { return LOOKUP_FAILED; }
        final Token headResult = lookup(item.asGraph().head, referenceName);
        if (headResult != LOOKUP_FAILED) { return headResult; }
        return lookup(item.asGraph().tail, referenceName);
    }

    @Override
    public Token getCanonical(Environment environment) {
        return lookup(environment.order, referenceName);
    }
}
