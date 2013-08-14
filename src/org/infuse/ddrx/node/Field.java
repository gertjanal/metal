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

package org.infuse.ddrx.node;

import org.infuse.ddrx.predicate.Expression;
import org.infuse.ddrx.predicate.Predicate;

public class Field implements Node {
    
    private final String _name;
    private final Expression _size;
    private final Predicate _pred;
    
    public Field(String name, Expression size, Predicate pred) {
        _name = name;
        _size = size;
        _pred = pred;
    }

    @Override
    public boolean eval() {
        // TODO Auto-generated method stub
        return false;
    }
    
}