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

package nl.minvenj.nfi.metal.expression.value;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;

public class Cat extends BinaryValueExpression {

    public Cat(final ValueExpression lop, final ValueExpression rop) {
        super(lop, rop);
    }

    @Override
    public OptionalValue eval(final Value lv, final Value rv, final Environment env, final Encoding enc) {
        final byte[] lb = lv.getValue();
        final byte[] rb = rv.getValue();
        final byte[] res = new byte[lb.length + rb.length];
        System.arraycopy(lb, 0, res, 0, lb.length);
        System.arraycopy(rb, 0, res, lb.length, rb.length);
        return OptionalValue.of(new Value(res, enc));
    }

}
