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

package io.parsingdata.metal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static io.parsingdata.metal.util.TokenDefinitions.eq;
import static io.parsingdata.metal.util.TokenDefinitions.eqRef;
import static io.parsingdata.metal.util.TokenDefinitions.notEqRef;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class BackTrackNameBindTest {

    private final Token _choiceRef = seq(any("a"),
                                         cho(seq(any("a"), eqRef("b", "a")),
                                             seq(notEqRef("b", "a"), any("c"))));

    private final Token _repeatRef = seq(rep(eq("a", 42)),
                                         rep(notEqRef("b", "a")));

    @Test
    public void choiceRefLeft() throws IOException {
        assertTrue(_choiceRef.parse(stream(1, 2, 2), enc()).succeeded);
    }

    @Test
    public void choiceRefRight() throws IOException {
        assertTrue(_choiceRef.parse(stream(1, 2, 3), enc()).succeeded);
    }

    @Test
    public void choiceRefNone() throws IOException {
        assertFalse(_choiceRef.parse(stream(1, 1, 2), enc()).succeeded);
    }

    @Test
    public void repeatRef() throws IOException {
        assertTrue(_repeatRef.parse(stream(42, 42, 42, 21, 21, 21), enc()).succeeded);
    }

}
