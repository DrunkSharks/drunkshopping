package com.drunk.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.Result;
import com.drunk.goods.feign.GoodsFeign;
import com.drunk.goods.pojo.Sku;
import com.drunk.search.dao.SkuEsMapper;
import com.drunk.search.pojo.SkuInfo;
import com.drunk.search.service.SkuService;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsFeign goodsFeign;

    /**
     * 将sku数据导入到ES
     */
    @Override
    public void importSku() {
        //获取sku正常可用商品
        Result<List<Sku>> skusResult = goodsFeign.findByStatus("1");
        //将Sku数据转换为Es.sku数据
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skusResult.getData()), SkuInfo.class);
        //将数据导入ES
        for (SkuInfo skuInfo : skuInfoList) {
            Map<String,Object> specMap = JSON.parseObject(skuInfo.getSpec());
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }

    /**
     * 根据关键词进行搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        //获取关键字的值
        String keywords = searchMap.get("keywords");

        //设置默认搜索华为产品
        if(StringUtils.isEmpty(keywords)){
            keywords = "华为";
        }

        //创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //设置分组条件   商品分类
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName").size(10000));
        //品牌分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName").size(10000));
        //规格分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(10000));

        //设置高亮条件
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style='color:red'>").postTags("</em>"));

        //设置关键字查询条件，根据sku名称搜索
        //nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));
        //设置多条件关键字查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"name","brandName","categoryName"));

        //多条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加分类条件过滤
        if(searchMap.get("category")!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName",searchMap.get("category")));
        }
        //添加品牌条件过滤
        if(searchMap.get("brand")!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
        }
        //添加规格条件过滤
        Set<String> keySet = searchMap.keySet();
        for (String speckey : keySet) {
            if(speckey.startsWith("spec_")){
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+speckey.substring(5)+".keyword",searchMap.get(speckey)));
            }
        }
        //添加价格条件过滤
        if(searchMap.get("price")!=null){
            String[] price = searchMap.get("price").replace("元","").replace("元以上","").split("-");
            if(price[1]!=null){
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").lte(Long.parseLong(price[1])));
            }
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(Long.parseLong(price[0])));
        }
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        //构建分页条件
        Integer pageNum = 1;  //页数，默认为第一页
        int size = 15;      //每页显示数量
        if(searchMap.get("pageNum")!=null){
            pageNum = Integer.parseInt(searchMap.get("pageNum"));
        }
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,size));
        //构建排序条件
        String sortRule = searchMap.get("sortRule");    //排序规则
        String sortField = searchMap.get("sortField");  //排序属性
        if(!StringUtils.isEmpty(sortRule) && !StringUtils.isEmpty(sortField)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equals("DESC")? SortOrder.DESC:SortOrder.ASC));
        }

        //构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                //sku集合
                List<T> skuInfos = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    //原命中sku信息
                    String sourceAsString = hit.getSourceAsString();
                    SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
                    //获取高亮显示信息
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightName = highlightFields.get("name");
                    //如果有高亮数据则设置高亮
                    if(highlightName!=null){
                        Text[] texts = highlightName.getFragments();
                        //拼接高亮显示信息
                        StringBuffer sb = new StringBuffer();
                        for (Text text : texts) {
                            sb.append(text);
                        }
                        //将高亮显示名称替换旧名称
                        skuInfo.setName(sb.toString());
                    }
                    skuInfos.add((T)skuInfo);
                }
                return new AggregatedPageImpl<T>(skuInfos,pageable,hits.getTotalHits(),searchResponse.getAggregations());
            }
        });

        //获取分组结果
        StringTerms categoryTerms = (StringTerms)skuPage.getAggregation("skuCategoryGroup");
        StringTerms brandTerms = (StringTerms)skuPage.getAggregation("skuBrandGroup");
        StringTerms specTerms = (StringTerms)skuPage.getAggregation("skuSpecGroup");

        //商品分类分组结果
        List<String> categoryList = getStringList(categoryTerms);
        //品牌分组结果
        List<String> brandList = getStringList(brandTerms);
        //品牌分类分组结果
        List<String> specList = getStringList(specTerms);

        //获取所有规格数据，将数据封装到一个Map集合
        Map<String, Set<String>> allspecMap = getSpecMap(specList);


        //返回结果
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rows",skuPage.getContent());
        resultMap.put("total",skuPage.getTotalElements());   //总记录数
        resultMap.put("totalPage",skuPage.getTotalPages());  //总页数
        resultMap.put("pageNum",pageNum);   //当前页
        resultMap.put("pageSize",size);  //每页显示数
        resultMap.put("categoryList",categoryList);
        resultMap.put("brandList",brandList);
        resultMap.put("allspecMap",allspecMap);

        return resultMap;
    }

    /**
     * 获取分类列表数据
     * @param stringTerms
     * @return
     */
    private List<String> getStringList(StringTerms stringTerms){
        List<String> categoryList = new ArrayList<>();

        if(stringTerms!=null){
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                categoryList.add(keyAsString);
            }
        }
        return categoryList;
    }

    /**
     * 获取所有规格数据，将数据封装到一个Map集合
     * @param specList
     * @return
     */
    private Map<String,Set<String>> getSpecMap(List<String> specList){
        //合并spec结果
        Map<String,Set<String>> allspecs = new HashMap<>();
        for (String spec : specList) {
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String speckey = entry.getKey();    //规格名字
                String specValue = entry.getValue();  //规格值
                Set<String> specSet = allspecs.get(speckey);
                if(specSet==null){
                    specSet = new HashSet();

                }
                //将规格值添加到set集合中
                specSet.add(specValue);
                allspecs.put(speckey,specSet);
            }
        }
        return allspecs;
    }
}
