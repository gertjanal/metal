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

import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

public class Pre extends Token {

    private final Token _op;
    private final Expression _pred;

    public Pre(final Token op, final Expression pred, final Encoding enc) {
        super(enc);
        _op = checkNotNull(op, "op");
        _pred = pred == null ? expTrue() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        if (!_pred.eval(env, enc)) { return new ParseResult(true, env); }
        final ParseResult res = _op.parse(scope, env.addBranch(this), enc);
        if (res.succeeded()) { return new ParseResult(true, res.getEnvironment().closeBranch()); }
        return new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + ", " + _pred + ")";
    }

}
