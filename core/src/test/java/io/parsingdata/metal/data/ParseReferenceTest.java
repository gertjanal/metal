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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.sub;
import static junit.framework.TestCase.assertFalse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class ParseReferenceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Token definition;
    private ParseReference reference;

    @Before
    public void setUp() {
        definition = sub(def("value", 1), con(0));
        reference = new ParseReference(0L, definition);
    }

    @Test
    public void state() {
        assertThat(reference.location, is(0L));
        assertThat(reference.getDefinition(), is(definition));
    }

    @Test
    public void toStringTest() {
        assertThat(reference.toString(), is("ref(@0)"));
    }

    @Test
    public void referenceIsAReference() {
        assertTrue(reference.isReference());
        assertThat(reference.asReference(), is(sameInstance(reference)));
    }

    @Test
    public void referenceIsNotAValue() {
        assertFalse(reference.isValue());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseReference to ParseValue");
        reference.asValue();
    }

    @Test
    public void referenceIsNotAGraph() {
        assertFalse(reference.isGraph());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseReference to ParseGraph");
        reference.asGraph();
    }

}