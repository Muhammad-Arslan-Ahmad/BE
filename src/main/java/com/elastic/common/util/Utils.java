package com.elastic.common.util;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public static final String PONG = "PONG";

    public static Map<String,Float> FIELDS = new HashMap<>();

    static {
        FIELDS.put("order_id", 0.1F);
        FIELDS.put("customer_name", 0.1F);
        FIELDS.put("customer_email", 0.1F);
        FIELDS.put("country", 0.1F);
        FIELDS.put("tracking_company", 0.1F);
        FIELDS.put("line_items.fulfillman_sku", 0.1F);
        FIELDS.put("line_items.sku", 0.1F);
        FIELDS.put("line_items.product_name", 0.1F);
        FIELDS.put("billing_address.name", 0.1F);
        FIELDS.put("billing_address.country", 0.1F);
    }

    public static final String API_KEY_HEADER = "Authorization";
    public static final String[] ORDER_INCLUDES = {"id", "order_number", "store", "order_id", "created_at",
            "total_cost_price", "total_sale_price", "sku", "status_man",
            "closed_at", "updated_at", "total_weight", "financial_status",
            "country", "tracking_company", "delivered_days", "note",
            "order_status_url", "is_brand", "customer_profit",
            "fulfillman_profit", "service_fee", "url", "status_men",
            "tracking_number", "tracking_status", "qty", "link",
            "line_items", "customer_email", "customer_name", "customer_phone"};
    public static final String[] ORDER_EXCLUDES = {"billing_address", "shipping_address"};

    public static final String[] PRODUCT_INCLUDES = {"id", "variant_name", "sku", "variant_sku", "cost_price", "profit", "weight", "product_name"
            ,"created_at", "link", "date_check", "store", "shopify_product_id", "variant_id", "fulfillment_service", "old_cost_price", "sale_price"
            , "count_price", "old_profit", "image", "logs", "updated_at","item"};
    public static final String[] PRODUCT_EXCLUDES = {"order_set"};

    public static boolean isOk(Integer value) {
        return !(value == null || value <= 0);
    }

    public static boolean isOk(Long value) {
        return !(value == null || value <= 0);
    }

    public static boolean isOk(String str) {
        return !(str == null || str.trim().isEmpty());
    }

    public static boolean isOk(Enum value) {
        return !(value == null);
    }

    public static String getDateToString(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }

        return (new SimpleDateFormat(dateFormat)).format(date);
    }

    public static Integer getIntegerFromObject(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Long) {
            return ((Long) object).intValue();
        } else if (object instanceof BigDecimal) {
            return ((BigDecimal) object).intValue();
        } else if (object instanceof BigInteger) {
            return ((BigInteger) object).intValue();
        } else if (object instanceof Short) {
            return ((Short) object).intValue();
        } else if (object instanceof Double) {
            return ((Double) object).intValue();
        } else if(object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (Throwable t) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static boolean isOk(Object value) {
        return !(value == null);
    }

    public static Date addDays(Date date, int day) {
        if(date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }
    public static Date getCurrentDate(String format) {
        try {
            if(!isOk(format)) {
                format = "dd/MM/yyyy";
            }
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(df.format(new Date()));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static Date getDate(String date) {
        if(!isOk(date)) {
            return null;
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(date);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static Date getDate(String date, boolean max) {
        if(!isOk(date)) {
            return null;
        }
        Date convertedDate;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            convertedDate = df.parse(date);
        } catch (Throwable t) {
            return null;
        }

        if(convertedDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertedDate);
        if(max) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 1);
        }

        return calendar.getTime();
    }

    public static Long getLongValue(String value) {
        try {
            return Long.parseLong(value);
        } catch (Throwable t) {
            return null;
        }
    }

    public static String getMd5(String text) {
        if (!isOk(text)) {
            return text;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes(Charset.defaultCharset()));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (Throwable r) {
            return "";
        }
    }

    public static boolean isOk(SearchResponse response) {
        return response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0;
    }

    public static BoolQueryBuilder getGlobalSearchQuery(String query) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (!Utils.isOk(query)) {
            queryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            queryBuilder.should(QueryBuilders.multiMatchQuery(query.trim(), "*")
                    .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX)
                    .operator(Operator.AND)
                    .boost(0.1f)
            );
            queryBuilder.should(QueryBuilders.multiMatchQuery(query.trim(), "*.keyword")
                    .operator(Operator.AND)
                    .boost(1.0f)
            );
            queryBuilder.should(QueryBuilders.queryStringQuery(query.trim())
                    .defaultOperator(Operator.AND)
                    .boost(0.01f)
                    .fuzziness(Fuzziness.AUTO)
            );
            queryBuilder.should(QueryBuilders.queryStringQuery("*"+query.trim()+"*")
                    .defaultOperator(Operator.OR)
                    .fuzziness(Fuzziness.AUTO)
                    .boost(0.00f)
            );
            queryBuilder.should(QueryBuilders.queryStringQuery(query.trim())
                    .defaultOperator(Operator.OR)
                    .fuzziness(Fuzziness.AUTO)
                    .boost(0.000f)
            );
            queryBuilder.minimumShouldMatch(1);
        }

        return queryBuilder;
    }
}
