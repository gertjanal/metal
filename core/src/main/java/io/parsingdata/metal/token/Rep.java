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
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

public class Rep extends Token {

    public final Token token;

    public Rep(final String name, final Token token, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ParseResult result = iterate(scope, environment.addBranch(this), encoding);
        return success(result.environment.closeBranch());
    }

    private ParseResult iterate(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ParseResult result = token.parse(scope, environment, encoding);
        if (result.succeeded) { return iterate(scope, result.environment, encoding); }
        return success(environment);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ")";
    }

}
