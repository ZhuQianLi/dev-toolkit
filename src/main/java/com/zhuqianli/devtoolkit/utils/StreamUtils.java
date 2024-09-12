package com.zhuqianli.devtoolkit.utils;

import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class StreamUtils {

    public static <T> List<T> filter(Collection<T> unfiltered, Predicate<T> predicate) {
        return unfiltered.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> Set<T> filterToSet(Collection<T> unfiltered, Predicate<T> predicate) {
        return unfiltered.stream().filter(predicate).collect(Collectors.toSet());
    }

    public static <T> List<T> filter(T[] unfiltered, Predicate<T> predicate) {
        return Arrays.stream(unfiltered).filter(predicate).collect(Collectors.toList());
    }

    public static <T> Set<T> filterToSet(T[] unfiltered, Predicate<T> predicate) {
        return Arrays.stream(unfiltered).filter(predicate).collect(Collectors.toSet());
    }

    public static <T> List<T> reject(Collection<T> unfiltered, Predicate<T> predicate) {
        return filter(unfiltered, predicate.negate());
    }

    public static <F, T> List<T> rejectAndMap(Collection<F> from, Predicate<F> predicate, Function<? super F, T> mapper) {
        return from.stream().filter(predicate.negate()).map(mapper).collect(Collectors.toList());
    }

    public static <F, T> Set<T> mapToSet(F[] from, Function<? super F, T> mapper) {
        return Arrays.stream(from).map(mapper).collect(Collectors.toSet());
    }

    public static <F, T> Set<T> mapToSet(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).collect(Collectors.toSet());
    }

    public static <F, T> Set<T> mapToSet(Stream<F> from, Function<? super F, T> mapper) {
        return from.map(mapper).collect(Collectors.toSet());
    }

    public static <F, T> List<T> mapFilterNull(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <F, T> Set<T> mapToSetFilterNull(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <F, T> List<T> map(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).collect(Collectors.toList());
    }

    public static <F, T> List<T> map(F[] from, Function<? super F, T> mapper) {
        return Arrays.stream(from).map(mapper).collect(Collectors.toList());
    }

    public static <F, T> List<T> map(Stream<F> from, Function<? super F, T> mapper) {
        return from.map(mapper).collect(Collectors.toList());
    }

    public static <F, T> List<T> filterAndMap(Collection<F> from, Predicate<F> predicate, Function<? super F, T> mapper) {
        return from.stream().filter(predicate).map(mapper).collect(Collectors.toList());
    }

    public static <F, T> Set<T> filterAndMapToSet(Collection<F> from, Predicate<F> predicate, Function<? super F, T> mapper) {
        return from.stream().filter(predicate).map(mapper).collect(Collectors.toSet());
    }

    public static <K, V> Map<K, V> toMap(Stream<V> from, Function<V, K> keyMapper) {
        return toMap(from, keyMapper, v -> v);
    }

    public static <K, V> Map<K, V> toMap(Collection<V> from, Function<V, K> keyMapper) {
        return toMap(from.stream(), keyMapper, v -> v);
    }

    public static <K, V, F> Map<K, V> toMap(Collection<F> from, Function<F, K> keyMapper, Function<F, V> valueMapper) {
        return toMap(from.stream(), keyMapper, valueMapper);
    }

    public static <K, V, F> Map<K, V> toMap(Stream<F> from, Function<F, K> keyMapper, Function<F, V> valueMapper) {
        return from.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <K, V, F> Map<K, V> toNullableMap(Collection<F> from, Function<F, K> keyMapper, Function<F, V> valueMapper) {
        return from.stream().collect(HashMap::new, (m, v) ->
                m.put(keyMapper.apply(v), valueMapper.apply(v)), HashMap::putAll);
    }

    public static <K, V> Map<K, List<V>> groupingBy(Collection<V> from, Function<V, K> classifier) {
        return groupingBy(from.stream(), classifier);
    }

    public static <K, V> Map<K, List<V>> groupingBy(Stream<V> fromStream, Function<V, K> classifier) {
        return fromStream.collect(Collectors.groupingBy(classifier));
    }

    public static <K, V> Map<K, List<V>> groupingByWithSort(Stream<V> fromStream, Function<V, K> classifier) {
        return fromStream.collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
    }

    public static <D, K, V> Map<K, Set<V>> groupingByAndToSet(Collection<D> from, Function<D, K> classifier, Function<D, V> valueMapper) {
        return from.stream().collect(Collectors.groupingBy(classifier, mapping(valueMapper, toSet())));
    }

    public static <D, K, V> Map<K, List<V>> groupingByAndToList(Collection<D> from, Function<D, K> classifier, Function<D, V> valueMapper) {
        return from.stream().collect(Collectors.groupingBy(classifier, mapping(valueMapper, toList())));
    }

    public static <S> BigDecimal sum(Collection<S> src, Function<S, BigDecimal> valueFn) {
        return src.stream().map(valueFn).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <S> BigDecimal sum(Collection<S> src, ToIntFunction<S> valueFn) {
        return src.stream().map(t -> new BigDecimal(valueFn.applyAsInt(t))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <S> BigDecimal sum(Collection<S> src, ToLongFunction<S> valueFn) {
        return src.stream().map(t -> new BigDecimal(valueFn.applyAsLong(t))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <T> List<T> flatMap(Collection<List<T>> from) {
        return from.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <R, T> List<T> flatMap(Collection<R> from, Function<R, Collection<T>> mapper) {
        return from.stream().map(mapper).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> Set<T> flatMapToSet(Collection<Set<T>> from) {
        return from.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static <R, T> Set<T> flatMapToSet(Collection<R> from, Function<R, Collection<T>> mapper) {
        return from.stream().map(mapper).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Nullable
    public static <T extends Comparable> T naturalOrderingMax(Stream<T> stream) {
        return stream.max(Ordering.natural()).orElse(null);
    }

    @Nullable
    public static <T extends Comparable> T naturalOrderingMin(Stream<T> stream) {
        return stream.min(Ordering.natural()).orElse(null);
    }

    public static long distinctCount(Stream<?> stream) {
        return stream.distinct().count();
    }

    public static <T> List<T> sorted(Collection<T> from, Comparator<T> comparator) {
        return from.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static Collection<Long> ints2longs(Collection<Integer> ints) {
        return ints.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList());
    }

    @Nullable
    public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        List<T> ts = collection.stream().filter(predicate).collect(Collectors.toList());
        return CollectionUtils.isEmpty(ts) ? null : ts.get(0);
    }

    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().allMatch(predicate);
    }

    /**
     * 按某个值排重
     * persons.stream().filter(distinctByKey(Person::getName))
     * <p>
     * https://stackoverflow.com/questions/23699371/java-8-distinct-by-property
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <K, V> Map<K, V> zipToMap(Stream<K> keyStream, Stream<V> valueStream) {
        return Streams.zip(keyStream, valueStream, Pair::of)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public static <T> Map<Boolean, List<T>> partitioningBy(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().collect(Collectors.partitioningBy(predicate));
    }

}
