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

import io.parsingdata.metal.expression.value.ValueExpression;

public class SourceFactory {

    public final ValueExpression dataExpression;
    public final Environment environment;

    public SourceFactory(final ValueExpression dataExpression, final Environment environment) {
        this.dataExpression = dataExpression;
        this.environment = environment;
    }

    public SourceFactory() {
        this(null, null);
    }

    public Source create(final long offset, final int size) {
        return new Source(dataExpression, environment, offset, size);
    }

}
