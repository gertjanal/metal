/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;

public class ImmutableList<T> implements Iterable<T> {

    public final T head;
    public final ImmutableList<T> tail;
    public final long size;

    public ImmutableList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ImmutableList(final T head, final ImmutableList<T> tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static <T> ImmutableList<T> create(final T head) {
        return new ImmutableList<T>().add(checkNotNull(head, "head"));
    }

    public static <T> ImmutableList<T> create(final T[] array) {
        return createFromArray(new ImmutableList<>(), checkNotNull(array, "array"), array.length - 1).computeResult();
    }

    private static <T> Trampoline<ImmutableList<T>> createFromArray(final ImmutableList<T> list, final T[] array, final int index) {
        if (index < 0) {
            return complete(() -> list);
        }
        return intermediate(() -> createFromArray(list.add(array[index]), array, index - 1));
    }

    public ImmutableList<T> add(final T head) {
        return new ImmutableList<>(checkNotNull(head, "head"), this);
    }

    public ImmutableList<T> add(final ImmutableList<T> list) {
        checkNotNull(list, "list");
        if (isEmpty()) {
            return list;
        }
        return addRecursive(reverse(list)).computeResult();
    }

    private Trampoline<ImmutableList<T>> addRecursive(final ImmutableList<T> list) {
        if (list.isEmpty()) {
            return complete(() -> this);
        }
        return intermediate(() -> add(list.head).addRecursive(list.tail));
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail;
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(head, ((ImmutableList)obj).head)
            && Objects.equals(tail, ((ImmutableList)obj).tail);
        // The size field is excluded from equals() and hashCode() because it is cached data.
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), head, tail);
    }

    @Override
    public Iterator<T> iterator() {
        final AtomicReference<ImmutableList<T>> next = new AtomicReference<>(reverse(this));
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                final ImmutableList<T> list = next.get();
                return !list.isEmpty();
            }

            @Override
            public T next() {
                final ImmutableList<T> list = next.get();
                if (list.isEmpty()) {
                    return null;
                }
                final T head = list.head;
                next.set(list.tail);

                return head;
            }
        };
    }

}
