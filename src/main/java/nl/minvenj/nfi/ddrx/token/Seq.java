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

package nl.minvenj.nfi.ddrx.token;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Seq extends Token {

    private final Token _l;
    private final Token _r;

    public Seq(Token l, Token r, Encoding enc) {
        super(enc);
        _l = l;
        _r = r;
    }

    public Seq(Token l, Token r) {
        this(l, r, null);
    }

    @Override
    protected boolean parseImpl(String scope, Environment env, Encoding enc) throws IOException {
        env.mark();
        final boolean ret = _l.parse(scope, env, enc) && _r.parse(scope, env, enc);
        if (ret) {
            env.clear();
        } else {
            env.reset();
        }
        return ret;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _l + "," + _r + ")";
    }

}