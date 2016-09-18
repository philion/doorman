package com.acmerocket.doorman.mongo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.util.Dates;
import com.acmerocket.doorman.util.Utils;
import com.google.common.collect.Sets;



public final class MongoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MongoUtil.class);
    private static final Set<String> QUERY_STOP_WORDS = Sets.newHashSet("to", "from", "sort", "limit", "offset", "skip");

    /**
     * Utility class
     */
    private MongoUtil() {}

    public static <T> Query<T> createQuery(Query<T> query, Map<String, List<String>> terms, String timeField) {
        query.disableValidation();   // Need this to avoid Morphia warnings generated by enum value comparisons

        // Add date filter to query
        query = queryDates(query, terms, timeField);

        // check sort
        query = querySort(query, terms);

        // check limit
        query = queryLimit(query, terms);

        // and skip/offset
        query = queryOffset(query, terms);

        // add any other filters, of the form "field=value" or "field=lt=value" or 
        query = queryTerms(query, terms);

        LOG.trace("Query: {} -> {}", terms, query);

        return query;
    }

    public static <T> Query<T> queryDates(Query<T> query, Map<String, List<String>> terms, String timeField) {
// FIXME
//        // check "from" and "to" dates
//        Date from = clock.from(removeFirst("from", terms));
//        if (from != null) {
//            query = query.field(timeField).greaterThanOrEq(from);
//        }
//
//        Date to = clock.from(removeFirst("to", terms));
//        if (to != null) {
//            query = query.field(timeField).lessThan(to);
//        }

        return query;
    }

    public static <T> Query<T> querySort(Query<T> query, Map<String, List<String>> terms) {
        List<String> sortFields = remove("sort", terms);
        if (sortFields != null) {
            for (String field : sortFields) {
                query = query.order(field);
            }
        }
        return query;
    }

    public static <T> Query<T> queryLimit(Query<T> query, Map<String, List<String>> terms) {
        //LOG.debug("terms={}", terms);

        int limit = Utils.toInt(removeFirst("limit", terms));
        //LOG.debug("Setting up limit={}, query={}, terms={}", limit, query, terms);
        if (limit >= 0) {
            query = query.limit(limit);
        }
        //LOG.debug("query={}", query);

        return query;
    }

    public static <T> Query<T> queryOffset(Query<T> query, Map<String, List<String>> terms) {
        int offset = Utils.toInt(removeFirst("offset", terms));
        if (offset >= 0) {
            query = query.offset(offset);
        }
        return query;
    }

    public static <T> Query<T> queryTerms(Query<T> query, Map<String, List<String>> terms) {
        for (Map.Entry<String, List<String>> entry : terms.entrySet()) {
            String fieldName = entry.getKey();
            if (!isStopTerm(fieldName)) {
                for (String value : entry.getValue()) {
                    // value is "string" or "exp=str"
                    query = queryExpValue(query, fieldName, value);
                }
            }
        }

        return query;
    }

    public static <T> Query<T> queryExpValue(Query<T> query, String fieldName, String expValue) {
        int idx = expValue.indexOf('=');
        if (idx > 0) { // really > 2, I think
            String expStr = expValue.substring(0, idx);
            RqlExpression exp = RqlExpression.parse(expStr);
            if (exp == null) {
                throw new IllegalArgumentException("Invalid query expression: " + expValue + " for field=" + fieldName);
            } else {
                String typeValue = expValue.substring(idx + 1); // can be type:value
                Object value = parseTypeValue(typeValue);

                switch (exp) {
                    case eq:
                        query.field(fieldName).equal(value);
                        break;
                    case lt:
                        query.field(fieldName).lessThan(value);
                        break;
                    case le:
                        query.field(fieldName).lessThanOrEq(value);
                        break;
                    case gt:
                        query.field(fieldName).greaterThan(value);
                        break;
                    case ge:
                        query.field(fieldName).greaterThanOrEq(value);
                        break;
                    case ne:
                        query.field(fieldName).notEqual(value);
                        break;
                    case ex:
                        query.field(fieldName).exists();
                        break;
                    case nx:
                        query.field(fieldName).doesNotExist();
                        break;
                    // other operations
                    // in
                    // nz - not-zero: exists and is not zero (or null)
                }
            }
        } else {
            query = query.field(fieldName).equal(expValue);
        }

        return query;
    }

    /**
     * Values can be of the format "value" or "type:value", where type in [string,number,boolean,epoch]
     */
    public static Object parseTypeValue(String typeValue) {
        Object value = typeValue; // default to string

        int idx = typeValue.indexOf(':');
        if (idx >= 5 && idx <= 7) {
            String type = typeValue.substring(0, idx);
            String strValue = typeValue.substring(idx + 1);
            switch (type) {
                case "string":
                    value = strValue;
                    break;
                case "number":
                    value = Utils.toNumberX(strValue);
                    break;
                case "boolean":
                    value = Utils.toBooleanX(strValue);
                    break;
                case "epoch":
                    value = Dates.toDateX(strValue);
                    break;
                default:
                    LOG.debug("Unknown type: {}, assuming part of string value", type);
            }
        }

//        if (Utils.isEmpty(value)) {
//            throw new IllegalArgumentException("Invalid type-value spec: " + typeValue);
//        }

        return value;
    }

    public static List<String> remove(String key, Map<String, List<String>> terms) {
        return terms.remove(key);
    }

    public static String removeFirst(String key, Map<String, List<String>> terms) {
        List<String> value = remove(key, terms);
        if (value != null && value.size() > 0) {
            return value.get(0);
        } else {
            return null;
        }
    }

    public static boolean isStopTerm(String key) {
        return QUERY_STOP_WORDS.contains(key);
    }

    public static String toString(Query<?> query) {
        QueryImpl<?> q = (QueryImpl<?>) query;
        return q.toString() + ", limit=" + q.getLimit() + ", offset=" + q.getOffset() + ", sort=" + q.getSortObject();
    }

    public static enum RqlExpression {
        eq, lt, le, gt, ge, ne, ex, nx /*, sum, mean, max, min, in, nin */;

        public static RqlExpression parse(String str) {
            try {
                return valueOf(str);
            } catch (Exception e) { /* ignore */ }
            return null;
        }
    }
}